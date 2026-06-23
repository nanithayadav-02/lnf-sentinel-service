package com.lnf.sentinel.service;

import com.lnf.dto.sentinel.IssueLinkDto;
import com.lnf.exception.LnFBadRequestException;
import com.lnf.exception.LnFEntityNotFoundException;
import com.lnf.sentinel.converter.IssueLinkConverter;
import com.lnf.sentinel.model.IssueLink;
import com.lnf.sentinel.model.enums.LinkType;
import com.lnf.sentinel.repository.IssueLinkRepository;
import com.lnf.sentinel.repository.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IssueLinkService {

    private final IssueLinkRepository repository;
    private final IssueRepository issueRepository;

    @Transactional
    public void createLink(UUID sourceIssueId, IssueLinkDto resource) {
        searchForIssueId(sourceIssueId);
        if (sourceIssueId.equals(resource.getTargetIssueId())) {
            throw new LnFBadRequestException("An issue cannot be linked to itself");
        }
        // Target must exist and be visible to the current tenant.
        searchForIssueId(resource.getTargetIssueId());
        if (repository.existsBySourceIssueIdAndTargetIssueIdAndLinkType(
                sourceIssueId, resource.getTargetIssueId(), LinkType.valueOf(resource.getLinkType()))) {
            throw new LnFBadRequestException("That link already exists");
        }
        IssueLinkConverter.toEntity(resource, new IssueLink());
    }

    @Transactional(readOnly = true)
    public List<IssueLinkDto> findByIssueId(UUID issueId) {
        searchForIssueId(issueId);
        List<IssueLink> entities = repository.findBySourceIssueId(issueId);
        return entities.stream().map(IssueLinkConverter::toDto).toList();
    }

    @Transactional
    public void deleteById(UUID linkId) {
        IssueLink link = repository.findById(linkId)
                .orElseThrow(() -> new LnFEntityNotFoundException("Link not found: " + linkId));
        repository.delete(link);
    }

    private void searchForIssueId(UUID id) {
        issueRepository.findById(id)
                .orElseThrow(() -> new LnFEntityNotFoundException("Issue not found: " + id));
    }

}
