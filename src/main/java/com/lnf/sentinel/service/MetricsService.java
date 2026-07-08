package com.lnf.sentinel.service;

import com.lnf.dto.sentinel.MetricSummaryDto;
import com.lnf.sentinel.model.Issue;
import com.lnf.sentinel.model.enums.IssueStatus;
import com.lnf.sentinel.model.enums.Severity;
import com.lnf.sentinel.model.enums.Trend;
import com.lnf.sentinel.repository.IssueRepository;
import com.lnf.tenant.core.context.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Dashboard headline numbers, scoped to the pinned tenant when present.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsService {

    private static final List<IssueStatus> CLOSED_STATES =
            List.of(IssueStatus.RESOLVED, IssueStatus.CLOSED);

    private final IssueRepository issueRepository;

    @Transactional(readOnly = true)
    public MetricSummaryDto summary(String tenantName) {

        boolean isTenant = tenantName != null;

        Specification<Issue> spec = isTenant
                ? scope(tenantName)
                : scope();

        long openTotal = issueRepository.count(spec.and(open()));
        long s1Count = issueRepository.count(spec.and(open()).and(severity(Severity.S1_CRITICAL)));
        long s2Count = issueRepository.count(spec.and(open()).and(severity(Severity.S2_HIGH)));
        long inProgress = issueRepository.count(spec.and(status(IssueStatus.IN_PROGRESS)));
        long awaitingTenant = issueRepository.count(spec.and(status(IssueStatus.AWAITING_TENANT)));
        long slaBreached = issueRepository.count(spec.and(open()).and(breachingSla()));
        long s1SlaBreached = issueRepository.count(spec.and(s1Breached()));

        return MetricSummaryDto.builder()
                .openIssues(buildOpenIssueCount(openTotal, tenantName))
                .s1Priority(buildS1IssueCount(s1Count, s1SlaBreached))
                .s2Priority(buildS2IssueCount(s2Count, tenantName))
                .inProgress(inProgress)
                .awaitingTenant(awaitingTenant)
                .slaBreached(buildS1BreachedCount(slaBreached))
                .build();
    }

    private MetricSummaryDto.OpenIssueDto buildOpenIssueCount(long openTotal, String tenantName) {

        LocalDate date = LocalDate.now().minusWeeks(1);

        LocalDate startDate = date.minusWeeks(1).with(DayOfWeek.MONDAY);
        LocalDate endDate = date.minusWeeks(1).with(DayOfWeek.SUNDAY);

        List<IssueStatus> statuses = List.of(IssueStatus.CLOSED, IssueStatus.RESOLVED);
        long lastWeekCount = issueRepository.countIssuesByStatusNotInAndDateAndTenantName(statuses, startDate, endDate,
                tenantName);
        long lastWeekDifference = Math.abs(openTotal - lastWeekCount);

        Trend trend;
        if (openTotal > lastWeekCount) {
            trend = Trend.DOWN;
        } else if (openTotal < lastWeekCount) {
            trend = Trend.UP;
        } else {
            trend = Trend.SAME;
        }
        return MetricSummaryDto.OpenIssueDto.builder().openTotal(openTotal)
                .lastWeekDifference(lastWeekDifference)
                .trend(String.valueOf(trend))
                .build();
    }

    private MetricSummaryDto.S1PriorityDto buildS1IssueCount(long s1Total, long s1Breached) {
        return MetricSummaryDto.S1PriorityDto.builder()
                .s1Count(s1Total)
                .breachedCount(s1Breached).build();
    }

    private MetricSummaryDto.S2PriorityDto buildS2IssueCount(long s2Total, String tenantName) {
        return MetricSummaryDto.S2PriorityDto.builder()
                .s2Count(s2Total)
                .tenantCount(issueRepository.countDistinctTenantsBySeverityAndTenantName(Severity.S2_HIGH, tenantName)).build();
    }

    private MetricSummaryDto.SlaBreached buildS1BreachedCount(long slaBreached) {
        return MetricSummaryDto.SlaBreached.builder()
                .slaBreachedCount(slaBreached)
                .isEscalationNeeded(slaBreached == 0 ? Boolean.FALSE : Boolean.TRUE).build();
    }

    private Specification<Issue> scope() {
        String tenant = TenantContext.getCurrentTenant();
        return (root, query, cb) ->
                tenant == null ? cb.conjunction() : cb.equal(root.get("tenantName"), tenant);
    }

    private Specification<Issue> scope(String tenantName) {
        return (root, query, cb) ->
                cb.equal(root.get("tenantName"), tenantName);
    }

    private Specification<Issue> open() {
        return (root, query, cb) ->
                cb.not(root.get("status").in(CLOSED_STATES));
    }

    private Specification<Issue> s1Breached() {
        return (root, query, cb) ->
                cb.equal(root.get("severity"), (Severity.S1_CRITICAL));
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
