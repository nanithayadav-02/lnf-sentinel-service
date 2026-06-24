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

import java.util.Date;
import java.util.Map;
import java.util.UUID;

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

        if (resource.getTenantCode() == null) {
            throw new LnFException("Tenant Id is required");
        }
        Issue issue = new Issue();

        issue.setIssueKey("ISSUE-" + issueRepository.nextIssueKeyNumber());
        issue.setSummary(resource.getSummary());
        issue.setDescription(resource.getDescription());

        issue.setTenantCode(resource.getTenantCode());
        issue.setTenantName(resource.getTenantName());

        issue.setSeverity(Severity.valueOf(resource.getSeverity()));
        issue.setPriority(Priority.valueOf(resource.getPriority()));
        issue.setCategory(Category.valueOf(resource.getCategory()));
        issue.setEnvironment(Environment.valueOf(resource.getEnvironment()));

        issue.setAssigneeId(resource.getAssigneeId());
        issue.setAssignee(resource.getAssignee());
        issue.setReportedBy(resource.getReportedBy());

        issue.setStatus(IssueStatus.NEW);

        Date detectedAt = resource.getDetectedAt() != null
                ? resource.getDetectedAt()
                : new Date();

        issue.setDetectedAt(detectedAt);

        issue.setSlaDueAt(resource.getSlaDueAt());
        issueRepository.save(issue);
    }

    private void resolveTenant(IssueDto resource) {
        if (tenantEnabled) {
            String tenantName = tenantFilterResolver.resolvePrefix();
            Tenant tenant = searchForTenantName(tenantName);
            resource.setTenantName(tenantName);
            resource.setTenantCode(UUID.fromString(tenant.getTenantCode()));
        } else {
            resource.setTenantName(resource.getTenantName());
            resource.setTenantCode(resource.getTenantCode());
        }
    }

    @Transactional(readOnly = true)
    public Page<IssueDto> list(String tenantName, UUID tenantCode, IssueStatus status, Severity severity,
                               UUID assigneeId, String search, Pageable pageable) {
        Specification<Issue> spec = Specification
                .where(tenantName(tenantName))
                .and(tenantCode(tenantCode))
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

    private Tenant searchForTenantName(String tenantCode) {
        return tenantRepository.findByTenantCode(tenantCode).orElseThrow(() ->
                new LnFEntityNotFoundException("Tenant with name [%s] does not exist".formatted(tenantCode)));
    }

    private Issue searchForIssueId(UUID id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new LnFEntityNotFoundException("Issue not found: " + id));
    }

    public static Specification<Issue> tenantCode(UUID tenantCode) {
        return (root, query, cb) -> {
            if (tenantCode == null) return null;
            return cb.equal(root.get("tenantCode"), tenantCode);
        };
    }

}
