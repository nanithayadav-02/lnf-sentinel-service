package com.lnf.sentinel.service;

import com.lnf.dto.sentinel.IssueStatusHistoryDto;
import com.lnf.sentinel.converter.IssueStatusHistoryConverter;
import com.lnf.sentinel.model.IssueStatusHistory;
import com.lnf.sentinel.repository.IssueStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class IssueStatusHistoryService {

    private final IssueStatusHistoryRepository repository;
    private final IssueAuditHistoryService issueAuditHistoryService;

    public IssueStatusHistoryDto create(IssueStatusHistoryDto dto) {

        log.info("Creating status history for issue: {}", dto.getIssueId());

        IssueStatusHistory entity = new IssueStatusHistory();

        IssueStatusHistoryConverter.toEntity(dto, entity);

        IssueStatusHistory saved = repository.save(entity);

        log.info("Status history created successfully. ID={}", saved.getId());

        return IssueStatusHistoryConverter.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<IssueStatusHistoryDto> getAll() {

        log.info("Fetching all issue status history records");

        return repository.findAll()
                .stream()
                .map(IssueStatusHistoryConverter::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<IssueStatusHistoryDto> getByIssueId(UUID issueId) {

        log.info("Fetching status history for issueId: {}", issueId);

        return repository.findByIssueId(issueId)
                .stream()
                .sorted(Comparator.comparing(IssueStatusHistory::getCreatedTime).reversed())
                .map(IssueStatusHistoryConverter::toDto)
                .toList();
    }

}
