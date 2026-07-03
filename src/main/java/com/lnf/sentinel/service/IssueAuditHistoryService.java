package com.lnf.sentinel.service;

import com.lnf.dto.sentinel.IssueAuditHistoryDto;
import com.lnf.exception.LnFEntityNotFoundException;
import com.lnf.sentinel.converter.IssueAuditHistoryConverter;
import com.lnf.sentinel.model.Issue;
import com.lnf.sentinel.model.IssueAuditHistory;
import com.lnf.sentinel.repository.IssueAuditHistoryRepository;
import com.lnf.sentinel.repository.IssueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IssueAuditHistoryService {

    private final IssueAuditHistoryRepository issueAuditHistoryRepository;
    private final IssueRepository issueRepository;

    @Transactional
    public void create(IssueAuditHistoryDto resource, UUID issueId) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new LnFEntityNotFoundException(
                        "Issue not found : " + issueId));

        IssueAuditHistory entity = new IssueAuditHistory();

        entity.setComment(resource.getComment());

        // Better to take the issue code from the Issue entity
        entity.setIssueCode(issue.getIssueKey());

        entity.setIssue(issue);

        issueAuditHistoryRepository.save(entity);

        log.info("Issue Audit History created successfully.");
    }

    @Transactional(readOnly = true)
    public IssueAuditHistoryDto findById(UUID id) {

        IssueAuditHistory entity = issueAuditHistoryRepository.findById(id)
                .orElseThrow(() ->
                        new LnFEntityNotFoundException(
                                "Issue Audit History not found : " + id));

        return IssueAuditHistoryConverter.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<IssueAuditHistoryDto> findAll() {

        return issueAuditHistoryRepository.findAll()
                .stream()
                .map(IssueAuditHistoryConverter::toDto)
                .collect(Collectors.toList());
    }

}