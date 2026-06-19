package com.lnf.sentinel.service;

import com.lnf.exception.LnFBadRequestException;
import com.lnf.exception.LnFEntityNotFoundException;
import com.lnf.sentinel.domain.Issue;
import com.lnf.sentinel.domain.IssueStatusHistory;
import com.lnf.sentinel.domain.enums.*;
import com.lnf.sentinel.dto.ChangeStatusRequest;
import com.lnf.sentinel.dto.CreateIssueRequest;
import com.lnf.sentinel.dto.IssueResponse;
import com.lnf.sentinel.dto.UpdateIssueRequest;
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

import static com.lnf.sentinel.service.IssueSpecifications.*;

@Service
@RequiredArgsConstructor
public class IssueService {

    private static final String KEY_PREFIX = "PRD-";

    private final IssueRepository issueRepository;
    private final IssueStatusHistoryRepository historyRepository;
    private final TenantRepository tenantRepository;

    @Transactional
    public IssueResponse create(CreateIssueRequest req) {
        Long tenantId = resolveTenantForWrite(req.tenantId());
        if (!tenantRepository.existsById(tenantId)) {
            throw new LnFBadRequestException("Unknown tenantId: " + tenantId);
        }

        Issue issue = new Issue();
        issue.setIssueKey(KEY_PREFIX + issueRepository.nextIssueKeyNumber());
        issue.setTenantId(tenantId);
        issue.setTitle(req.title());
        issue.setDescription(req.description());
        issue.setSeverity(req.severity() != null ? req.severity() : Severity.S3_MEDIUM);
        issue.setPriority(req.priority() != null ? req.priority() : Priority.P3);
        issue.setCategory(req.category() != null ? req.category() : Category.BUG);
        issue.setEnvironment(req.environment() != null ? req.environment() : Environment.PRODUCTION);
        issue.setAffectedService(req.affectedService());
        issue.setReportedBy(req.reportedBy());
        issue.setAssigneeId(req.assigneeId());
        issue.setStatus(IssueStatus.NEW);

        OffsetDateTime detectedAt = req.detectedAt() != null ? req.detectedAt() : OffsetDateTime.now();
        issue.setDetectedAt(detectedAt);
        issue.setSlaDueAt(detectedAt.plus(issue.getSeverity().slaTarget()));

        Issue saved = issueRepository.save(issue);
        recordHistory(saved, null, IssueStatus.NEW, req.reportedBy(), "Issue created");
        return IssueResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<IssueResponse> list(Long tenantId, IssueStatus status, Severity severity,
                                    Long assigneeId, String search, Pageable pageable) {
        Specification<Issue> spec = Specification
                .where(tenantScope(tenantId))
                .and(status(status))
                .and(severity(severity))
                .and(assigneeId(assigneeId))
                .and(search(search));
        return issueRepository.findAll(spec, pageable).map(IssueResponse::from);
    }

    @Transactional(readOnly = true)
    public IssueResponse get(Long id) {
        return IssueResponse.from(loadScoped(id));
    }

    @Transactional
    public IssueResponse update(Long id, UpdateIssueRequest req) {
        Issue issue = loadScoped(id);
        if (req.title() != null) issue.setTitle(req.title());
        if (req.description() != null) issue.setDescription(req.description());
        if (req.severity() != null) issue.setSeverity(req.severity());
        if (req.priority() != null) issue.setPriority(req.priority());
        if (req.category() != null) issue.setCategory(req.category());
        if (req.environment() != null) issue.setEnvironment(req.environment());
        if (req.affectedService() != null) issue.setAffectedService(req.affectedService());
        if (req.assigneeId() != null) issue.setAssigneeId(req.assigneeId());
        if (req.fixVersion() != null) issue.setFixVersion(req.fixVersion());
        return IssueResponse.from(issueRepository.save(issue));
    }

    @Transactional
    public IssueResponse changeStatus(Long id, ChangeStatusRequest req) {
        Issue issue = loadScoped(id);
        IssueStatus from = issue.getStatus();
        IssueStatus to = req.status();

        if (to.isResolutionState()) {
            if (req.resolution() == null) {
                throw new LnFBadRequestException("A resolution is required when moving to " + to);
            }
            issue.setResolution(req.resolution());
            if (req.rootCause() != null) issue.setRootCause(req.rootCause());
            if (req.fixVersion() != null) issue.setFixVersion(req.fixVersion());
            if (issue.getResolvedAt() == null) issue.setResolvedAt(OffsetDateTime.now());
        } else if (to == IssueStatus.REOPENED) {
            issue.setResolvedAt(null);
            issue.setResolution(null);
        }

        issue.setStatus(to);
        Issue saved = issueRepository.save(issue);
        recordHistory(saved, from, to, req.changedBy(), req.note());
        return IssueResponse.from(saved);
    }

    // ---- helpers ----------------------------------------------------------

    /**
     * Load an issue scoped to the pinned tenant; 404 (never leak) on cross-tenant access.
     */
    private Issue loadScoped(Long id) {
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
        Long effective = TenantContext.isSet() ? TenantContext.getTenantId() : requestedTenantId;
        return tenantId(effective);
    }

    private Long resolveTenantForWrite(Long requestedTenantId) {
        if (TenantContext.isSet()) {
            Long pinned = TenantContext.getTenantId();
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
        h.setIssueId(issue.getId());
        h.setFromStatus(from);
        h.setToStatus(to);
        h.setChangedBy(changedBy);
        h.setNote(note);
        historyRepository.save(h);
    }
}
