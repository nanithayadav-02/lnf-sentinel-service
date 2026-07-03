package com.lnf.sentinel.service;

import com.lnf.dto.sentinel.IssueDto;
import com.lnf.exception.LnFEntityNotFoundException;
import com.lnf.exception.LnFException;
import com.lnf.sentinel.converter.IssueConverter;
import com.lnf.sentinel.model.Issue;
import com.lnf.sentinel.model.IssueStatusHistory;
import com.lnf.sentinel.model.Tenant;
import com.lnf.sentinel.model.enums.*;
import com.lnf.sentinel.repository.IssueRepository;
import com.lnf.sentinel.repository.IssueStatusHistoryRepository;
import com.lnf.sentinel.repository.TenantRepository;
import com.lnf.sentinel.tenant.TenantFilterResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.lnf.sentinel.service.IssueSpecifications.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class IssueService {

    private static final String KEY_PREFIX = "PRD-";
    private final IssueRepository issueRepository;
    private final IssueStatusHistoryRepository historyRepository;
    private final TenantRepository tenantRepository;
    private final TenantFilterResolver tenantFilterResolver;
    @Value("${lnf.tenant.enabled}")
    private boolean tenantEnabled;

    @Transactional
    public void create(IssueDto resource) {

        resolveTenant(resource);
        if (resource.getTenantId() == null) {
            throw new LnFException("Tenant Id is required");
        }

        Issue issue = new Issue();
        Long seq = issueRepository.nextIssueKeyNumber();
        issue.setIssueKey("ISSUE-" + seq);
        issue.setSummary(resource.getSummary());
        issue.setDescription(resource.getDescription());
        issue.setTenantId(resource.getTenantId());
        issue.setTenantName(resource.getTenantName());
        issue.setAssigneeId(resource.getAssigneeId());
        issue.setAssignee(resource.getAssignee());
        issue.setReportedBy(resource.getReportedBy());
        issue.setAffectedService(resource.getAffectedService());
        issue.setSeverity(Severity.valueOf(resource.getSeverity()));
        issue.setPriority(Priority.valueOf(resource.getPriority()));
        issue.setCategory(Category.valueOf(resource.getCategory()));
        issue.setEnvironment(Environment.valueOf(resource.getEnvironment()));
        issue.setStatus(IssueStatus.valueOf(resource.getStatus()));
        issue.setDetectedAt(resource.getDetectedAt());
        issue.setSlaDueAt(resource.getSlaDueAt());
        Issue saved = issueRepository.saveAndFlush(issue);
        log.info("Issue saved successfully. ID={}", saved.getId());
    }

    private void resolveTenant(IssueDto resource) {

        if (tenantEnabled) {
            String tenantName = tenantFilterResolver.resolvePrefix();

            Tenant tenant = searchForTenantName(tenantName);
            resource.setTenantName(tenant.getName());
            resource.setTenantId(tenant.getId());

        } else {
            if (resource.getTenantId() == null) {
                throw new LnFException("Tenant Id is required");
            }
        }
    }

    private Tenant searchForTenantName(String tenantName) {

        return tenantRepository.findByName(tenantName)
                .orElseThrow(() ->
                        new LnFEntityNotFoundException(
                                "Tenant with name [" + tenantName + "] does not exist"
                        ));
    }

    @Transactional(readOnly = true)
    public Page<IssueDto> list(String tenantName, UUID tenantId, IssueStatus status, Severity severity,
                               UUID assigneeId, String search, Pageable pageable) {
        Specification<Issue> spec = Specification
                .where(tenantName(tenantName))
                .and(tenantId(tenantId))
                .and(status(status))
                .and(severity(severity))
                .and(assigneeId(assigneeId))
                .and(search(search));
        return issueRepository.findAll(spec, pageable).map(IssueConverter::toTransportModel);
    }

    @Transactional(readOnly = true)
    public IssueDto findById(UUID id) {
        return IssueConverter.toTransportModel(searchForIssueId(id));
    }

    @Transactional
    public void update(UUID id, IssueDto resource) {
        Issue issue = searchForIssueId(id);
        Issue entity = IssueConverter.toEntityModel(resource, new Issue());
        issueRepository.save(entity);
        if (!issue.getStatus().equals(entity.getStatus())) {
            recordHistory(entity, issue.getStatus(), entity.getStatus(), "IssueUpdated");
        }
    }

    @Transactional
    public void changeStatus(UUID id, Map<String, Object> request) {
        Issue issue = searchForIssueId(id);
        IssueStatus from = issue.getStatus();
        IssueStatus to = IssueStatus.valueOf((String) request.get("status"));
        String notes = (String) request.get("notes");
        if (!from.equals(to)) {
            issue.setStatus(to);
            issueRepository.save(issue);
            recordHistory(issue, from, to, notes);
        }
    }

    public void deleteById(UUID issueId) {
        Issue issue = searchForIssueId(issueId);
        try {
            issueRepository.delete(issue);
            log.debug("Issue with Id {} successfully deleted", issueId);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to delete Issue with Id [%s]".formatted(issueId);
            throw new LnFException(errorMessage);
        }
    }

    private void recordHistory(Issue issue, IssueStatus from, IssueStatus to, String notes) {
        IssueStatusHistory h = new IssueStatusHistory();
        h.setIssueId(issue.getId());
        h.setFromStatus(from);
        h.setToStatus(to);
        h.setNotes(notes);
        historyRepository.save(h);
    }


    private Issue searchForIssueId(UUID id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new LnFEntityNotFoundException("Issue not found: " + id));
    }

    public static Specification<Issue> tenantId(UUID tenantId) {
        return (root, query, cb) -> {
            if (tenantId == null) return null;
            return cb.equal(root.get("tenantId"), tenantId);
        };
    }

    public Map<String, Object> getIssueCountBySeverity() {

        List<Object[]> results = issueRepository.getIssueCountByseverity();
        List<Map<String, Object>> severity = new ArrayList<>();
        List<Map<String, Object>> status = new ArrayList<>();

        for (Object[] row : results) {
            String key = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            Map<String, Object> map = new HashMap<>();
            map.put("severity", key);
            map.put("count", count);
            if (key.startsWith("S1") || key.startsWith("S2")
                    || key.startsWith("S3") || key.startsWith("S4")) {
                severity.add(map);
            } else {
                status.add(map);
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("severity", severity);
        result.put("status", status);
        return result;
    }
}

