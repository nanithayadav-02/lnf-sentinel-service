package com.lnf.sentinel.service;

import com.lnf.sentinel.domain.Issue;
import com.lnf.sentinel.domain.enums.IssueStatus;
import com.lnf.sentinel.domain.enums.Severity;
import com.lnf.sentinel.dto.MetricsSummaryResponse;
import com.lnf.sentinel.repository.IssueRepository;
import com.lnf.sentinel.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

import static com.lnf.sentinel.service.IssueSpecifications.severity;
import static com.lnf.sentinel.service.IssueSpecifications.tenantId;

/** Dashboard headline numbers, scoped to the pinned tenant when present. */
@Service
@RequiredArgsConstructor
public class MetricsService {

    private static final List<IssueStatus> CLOSED_STATES =
            List.of(IssueStatus.RESOLVED, IssueStatus.CLOSED);

    private final IssueRepository issueRepository;

    @Transactional(readOnly = true)
    public MetricsSummaryResponse summary() {
        long openTotal       = issueRepository.count(scope().and(open()));
        long s1Count         = issueRepository.count(scope().and(open()).and(severity(Severity.S1_CRITICAL)));
        long s2Count         = issueRepository.count(scope().and(open()).and(severity(Severity.S2_HIGH)));
        long inProgress      = issueRepository.count(scope().and(status(IssueStatus.IN_PROGRESS)));
        long awaitingTenant  = issueRepository.count(scope().and(status(IssueStatus.AWAITING_TENANT)));
        long slaBreached     = issueRepository.count(scope().and(open()).and(breachingSla()));
        return new MetricsSummaryResponse(openTotal, s1Count, s2Count, inProgress, awaitingTenant, slaBreached);
    }

    private Specification<Issue> scope() {
        return tenantId(TenantContext.getTenantId()); // null => cross-tenant (all)
    }

    private Specification<Issue> open() {
        return (root, query, cb) -> cb.not(root.get("status").in(CLOSED_STATES));
    }

    private Specification<Issue> status(IssueStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    private Specification<Issue> breachingSla() {
        return (root, query, cb) -> cb.and(
                cb.isNotNull(root.get("slaDueAt")),
                cb.lessThan(root.get("slaDueAt"), OffsetDateTime.now()));
    }
}
