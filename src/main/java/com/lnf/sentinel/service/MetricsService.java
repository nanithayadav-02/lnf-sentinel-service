package com.lnf.sentinel.service;

import com.lnf.dto.sentinel.MetricSummaryDto;

import com.lnf.sentinel.model.Issue;
import com.lnf.sentinel.model.enums.IssueStatus;
import com.lnf.sentinel.model.enums.Severity;
import com.lnf.sentinel.repository.IssueRepository;
import com.lnf.tenant.core.context.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

;

/**
 * Dashboard headline numbers, scoped to the pinned tenant when present.
 */
@Service
@RequiredArgsConstructor
public class MetricsService {

    private static final List<IssueStatus> CLOSED_STATES =
            List.of(IssueStatus.RESOLVED, IssueStatus.CLOSED);

    private final IssueRepository issueRepository;

    @Transactional(readOnly = true)
    public MetricSummaryDto summary(UUID tenantId) {

        boolean isTenant = tenantId != null;

        Specification<Issue> spec = isTenant
                ? scope(tenantId)
                : scope();

        long openTotal = issueRepository.count(spec.and(open()));
        long s1Count = issueRepository.count(spec.and(open()).and(severity(Severity.S1_CRITICAL)));
        long s2Count = issueRepository.count(spec.and(open()).and(severity(Severity.S2_HIGH)));
        long inProgress = issueRepository.count(spec.and(status(IssueStatus.IN_PROGRESS)));
        long awaitingTenant = issueRepository.count(spec.and(status(IssueStatus.AWAITING_TENANT)));
        long slaBreached = issueRepository.count(spec.and(open()).and(breachingSla()));

        return MetricSummaryDto.builder()
                .openTotal(openTotal)
                .s1Count(s1Count)
                .s2Count(s2Count)
                .inProgress(inProgress)
                .awaitingTenant(awaitingTenant)
                .slaBreached(slaBreached)
                .build();
    }

    private Specification<Issue> scope() {
        String tenant = TenantContext.getCurrentTenant();
        return (root, query, cb) ->
                tenant == null ? cb.conjunction() : cb.equal(root.get("tenantName"), tenant);
    }

    private Specification<Issue> scope(UUID tenantId) {
        return (root, query, cb) ->
                cb.equal(root.get("tenantId"), tenantId);
    }

    private Specification<Issue> open() {
        return (root, query, cb) ->
                cb.not(root.get("status").in(CLOSED_STATES));
    }

    private Specification<Issue> status(IssueStatus status) {
        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }

    private Specification<Issue> severity(Severity severity) {
        return (root, query, cb) ->
                cb.equal(root.get("severity"), severity);
    }

    private Specification<Issue> breachingSla() {
        return (root, query, cb) -> cb.and(
                cb.isNotNull(root.get("slaDueAt")),
                cb.lessThan(root.get("slaDueAt"), new Date())
        );
    }
}
