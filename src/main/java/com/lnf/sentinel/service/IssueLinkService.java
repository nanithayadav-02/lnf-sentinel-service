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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class IssueLinkService {

    private final IssueLinkRepository repository;
    private final IssueRepository issueRepository;
    private final IssueAuditHistoryService issueAuditHistoryService;

    @Transactional
    public void createLink(UUID sourceIssueId, IssueLinkDto resource) {

        searchForIssueId(sourceIssueId);

        if (sourceIssueId.equals(resource.getTargetIssueId())) {
            throw new LnFBadRequestException("An issue cannot be linked to itself");
        }

        searchForIssueId(resource.getTargetIssueId());

        LinkType linkType = LinkType.valueOf(resource.getLinkType());

        if (repository.existsBySourceIssueIdAndTargetIssueIdAndLinkType(
                sourceIssueId,
                resource.getTargetIssueId(),
                linkType)) {

            throw new LnFBadRequestException("That link already exists");
        }

        IssueLink entity = IssueLinkConverter.toEntity(resource, new IssueLink());

        entity.setSourceIssueId(sourceIssueId);

        repository.save(entity);   // <-- MISSING LINE

        issueAuditHistoryService.log(
                "ISSUE",
                "CREATE",
                entity.getId(),
                "Issue Audit History created successfully"+resource.getCreatedBy(),
                resource
        );
    }

    @Transactional(readOnly = true)
    public List<Map<String,Object>> findByIssueId(UUID issueId) {
        searchForIssueId(issueId);
        List<Object[]> entities = repository.findByIssuesDetails(issueId);
        return entities.
                stream().map(row ->{
                    Map<String,Object> map=new HashMap<>();
                    map.put("targetId",row[0]);
                    map.put("id",row[1]);
                    map.put("rootCause",row[2]);
                    map.put("issueKey",row[3]);
                    map.put("summary",row[4]);
                    map.put("linkType",row[5]);
                    return map;
                        }).toList();
    }

    @Transactional
    public void deleteById(UUID linkId) {
        IssueLink link = repository.findById(linkId)
                .orElseThrow(() -> new LnFEntityNotFoundException("Link not found: " + linkId));
        repository.delete(link);
        issueAuditHistoryService.log(
                "ISSUE-Link",
                "Delete",
                link.getId(),
                "Issue Link Deleted"+link.getCreatedBy(),
                link
        );
    }

    private void searchForIssueId(UUID id) {
        issueRepository.findById(id)
                .orElseThrow(() -> new LnFEntityNotFoundException("Issue not found: " + id));
    }

}
