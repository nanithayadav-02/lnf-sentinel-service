package com.lnf.sentinel.service;

import com.lnf.sentinel.AbstractIntegrationTest;
import com.lnf.sentinel.domain.enums.*;
import com.lnf.sentinel.dto.ChangeStatusRequest;
import com.lnf.sentinel.dto.CreateIssueRequest;
import com.lnf.sentinel.dto.MetricsSummaryResponse;
import com.lnf.sentinel.tenant.TenantContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class MetricsServiceTest extends AbstractIntegrationTest {

    @Autowired IssueService issueService;
    @Autowired MetricsService metricsService;

    @BeforeEach
    void pin() {
        TenantContext.setTenantId(ACME);
    }

    private Long create(Severity sev, OffsetDateTime detectedAt) {
        return issueService.create(new CreateIssueRequest(
                "m", null, null, sev, Priority.P2, Category.BUG,
                Environment.PRODUCTION, "svc", ENG1, ENG1, detectedAt)).id();
    }

    @Test
    void summaryCountsOpenSeverityAndBreaches() {
        create(Severity.S1_CRITICAL, OffsetDateTime.now());
        Long s2 = create(Severity.S2_HIGH, OffsetDateTime.now());
        // breaching: detected 10h ago, S1 target 4h -> overdue
        create(Severity.S1_CRITICAL, OffsetDateTime.now().minusHours(10));

        issueService.changeStatus(s2, new ChangeStatusRequest(
                IssueStatus.IN_PROGRESS, null, null, null, "working", ENG1));

        MetricsSummaryResponse m = metricsService.summary();
        assertThat(m.openTotal()).isEqualTo(3);
        assertThat(m.s1Count()).isEqualTo(2);
        assertThat(m.s2Count()).isEqualTo(1);
        assertThat(m.inProgress()).isEqualTo(1);
        assertThat(m.slaBreached()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void summaryIsTenantScoped() {
        create(Severity.S1_CRITICAL, OffsetDateTime.now());
        TenantContext.setTenantId(GLOBEX);
        assertThat(metricsService.summary().openTotal()).isZero();
    }
}
