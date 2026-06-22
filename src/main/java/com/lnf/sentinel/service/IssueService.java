package com.lnf.sentinel.service;

import com.lnf.dto.sentinel.IssueDto;
import com.lnf.exception.LnFBadRequestException;
import com.lnf.exception.LnFEntityNotFoundException;
import com.lnf.sentinel.converter.IssueConverter;
import com.lnf.sentinel.model.Issue;
import com.lnf.sentinel.model.IssueStatusHistory;
import com.lnf.sentinel.model.enums.*;
import com.lnf.sentinel.repository.IssueRepository;
import com.lnf.sentinel.repository.IssueStatusHistoryRepository;
import com.lnf.sentinel.repository.TenantRepository;
import com.lnf.sentinel.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.lnf.sentinel.service.IssueSpecifications.*;

@Service
@RequiredArgsConstructor
public class IssueService {

    private static final String KEY_PREFIX = "PRD-";

    private final IssueRepository issueRepository;
    private final IssueStatusHistoryRepository historyRepository;
    private final TenantRepository tenantRepository;
    private final TenantContext tenantContext;

    @Transactional
    public IssueDto create(IssueDto req) {
        UUID tenantId = resolveTenantForWrite(req.getTenantId());
        if (!tenantRepository.existsById(tenantId)) {
            throw new LnFBadRequestException("Unknown tenantId: " + tenantId);
        }

        Issue issue = new Issue();
        issue.setIssueKey(KEY_PREFIX + issueRepository.nextIssueKeyNumber());
        issue.setTenantId(req.getTenantId());
        issue.setTitle(req.getTitle());
        issue.setDescription(req.getDescription());
        issue.setSeverity((Severity) Objects.requireNonNullElse(req.getSeverity(),Severity.S3_MEDIUM));
        issue.setPriority((Priority) Objects.requireNonNullElse(req.getPriority(),Priority.P3));
        issue.setCategory((Category) Objects.requireNonNullElse(req.getCategory(),Category.BUG));
        issue.setEnvironment((Environment) Objects.requireNonNullElse(req.getEnvironment(),Environment.PRODUCTION));
        issue.setAffectedService(req.getAffectedService());
        issue.setReportedBy(req.getReportedBy());
        issue.setAssigneeId(req.getAssigneeId());
        issue.setStatus(IssueStatus.NEW);

        OffsetDateTime detectedAt = req.getDetectedAt() != null ? req.getDetectedAt() : OffsetDateTime.now();
        issue.setDetectedAt(detectedAt);
        issue.setSlaDueAt(detectedAt.plus(issue.getSeverity().slaTarget()));

        Issue saved = issueRepository.save(issue);
        recordHistory(saved, null, IssueStatus.NEW, req.getReportedBy(), "Issue created");
        return IssueConverter.toTransportModel(saved);
    }

    @Transactional(readOnly = true)
    public Page<IssueDto> list(Long tenantId, IssueStatus status, Severity severity,
                               Long assigneeId, String search, Pageable pageable) {
        Specification<Issue> spec = Specification
                .where(tenantScope(tenantId))
                .and(status(status))
                .and(severity(severity))
                .and(assigneeId(assigneeId))
                .and(search(search));
        return issueRepository.findAll(spec, pageable).map(IssueConverter::toTransportModel);
    }

    @Transactional(readOnly = true)
    public IssueDto get(UUID id) {
      return IssueConverter.toTransportModel(loadScoped(id));
    }

    @Transactional
    public IssueDto update(UUID id, IssueDto req) {
        Issue issue = loadScoped(id);
        if (req.getTitle() != null) issue.setTitle(req.getTitle());
        if (req.getDescription() != null) issue.setDescription(req.getDescription());
        if (req.getSeverity() != null) issue.setSeverity(Severity.valueOf(req.getSeverity()));
        if (req.getPriority() != null) issue.setPriority(Priority.valueOf(req.getPriority()));
        if (req.getCategory() != null) issue.setCategory(Category.valueOf(req.getCategory()));
        if (req.getEnvironment() != null) issue.setEnvironment(Environment.valueOf(req.getEnvironment()));
        if (req.getAffectedService() != null) issue.setAffectedService(req.getAffectedService());
        if (req.getAssigneeId()!= null) issue.setAssigneeId(req.getAssigneeId());
        if (req.getFixVersion() != null) issue.setFixVersion(req.getFixVersion());
        return IssueConverter.toTransportModel(issueRepository.save(issue));
    }

    @Transactional
    public IssueDto changeStatus(UUID id, IssueDto req) {
        Issue issue = loadScoped(id);
        IssueStatus from = issue.getStatus();
        IssueStatus to = IssueStatus.valueOf(req.getStatus());

        if (to.isResolutionState()) {
            if (req.getResolution()== null) {
                throw new LnFBadRequestException("A resolution is required when moving to " + to);
            }
            issue.setResolution(Resolution.valueOf(req.getResolution()));
            if (req.getRootCause() != null) issue.setRootCause(req.getRootCause());
            if (req.getFixVersion()!= null) issue.setFixVersion(req.getFixVersion());
            if (issue.getResolvedAt() == null) issue.setResolvedAt(OffsetDateTime.now());
        } else if (to == IssueStatus.REOPENED) {
            issue.setResolvedAt(null);
            issue.setResolution(null);
        }

        issue.setStatus(to);
        Issue saved = issueRepository.save(issue);
        recordHistory(saved, from, to, req.changedBy(), req.note());
        return IssueConverter.toTransportModel(saved);
    }

    // ---- helpers ----------------------------------------------------------

    /**
     * Load an issue scoped to the pinned tenant; 404 (never leak) on cross-tenant access.
     */
    private Issue loadScoped(UUID id) {
        if (TenantContext.isSet()) {
            return issueRepository.findByIdAndTenantId(id, TenantContext.getTenantId())
                    .orElseThrow(() -> new LnFEntityNotFoundException("Issue not found: " + id));
        }
        return issueRepository.findById(id)
                .orElseThrow(() -> new LnFEntityNotFoundException("Issue not found: " + id));
    }

    /**
     * A pinned tenant overrides any requested tenant filter; otherwise honour the filter.
     */
    private Specification<Issue> tenantScope(Long requestedTenantId) {
        Long effective = TenantContext.isSet() ? TenantContext.getTenantId() : (UUID) requestedTenantId;
        return tenantId(effective);
    }

    private UUID resolveTenantForWrite(UUID requestedTenantId) {
        if (TenantContext.isSet()) {
            UUID pinned = TenantContext.getTenantId();
            if (requestedTenantId != null && !requestedTenantId.equals(pinned)) {
                throw new LnFBadRequestException("tenantId does not match the pinned tenant");
            }
            return pinned;
        }
        if (requestedTenantId == null) {
            throw new LnFBadRequestException("tenantId is required when no tenant is pinned");
        }
        return requestedTenantId;
    }

    private void recordHistory(Issue issue, IssueStatus from, IssueStatus to, Long changedBy, String note) {
        IssueStatusHistory h = new IssueStatusHistory();
        h.setIssueId();
        h.setFromStatus(from);
        h.setToStatus(to);
        h.setChangedBy(changedBy);
        h.setNote(note);
        historyRepository.save(h);
    }
}
