package com.lnf.sentinel.service;

import com.lnf.dto.sentinel.MetricSummaryDto;
import com.lnf.sentinel.repository.IssueRepository;
import com.lnf.tenant.core.context.TenantContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetricsServiceTest {

    @Mock
    private IssueRepository issueRepository;

    @InjectMocks
    private MetricsService metricsService;

    @BeforeEach
    void setup() {
        TenantContext.clear(); // safe reset if available
    }

    @Test
    void shouldReturnMetricSummary() {
        try (MockedStatic<TenantContext> mockedTenant = mockStatic(TenantContext.class)) {

            mockedTenant.when(TenantContext::getCurrentTenant).thenReturn("TENANT_1");
            when(issueRepository.count(any(Specification.class))).thenReturn(10L);

            MetricSummaryDto result = metricsService.summary("lnf");

            assertNotNull(result);
            assertEquals(10L, result.getOpenIssues().getOpenTotal());
            assertEquals(10L, result.getS1Priority().getS1Count());
            assertEquals(10L, result.getS2Priority().getS2Count());
            assertEquals(10L, result.getInProgress());
            assertEquals(10L, result.getAwaitingTenant());
            assertEquals(10L, result.getSlaBreached().getSlaBreachedCount());

            verify(issueRepository, times(7)).count(any(Specification.class));
        }
    }

    @Test
    void shouldHandleNullTenantContext() {
        try (MockedStatic<TenantContext> mockedTenant = mockStatic(TenantContext.class)) {

            mockedTenant.when(TenantContext::getCurrentTenant).thenReturn(null);
            when(issueRepository.count(any(Specification.class))).thenReturn(5L);

            MetricSummaryDto result = metricsService.summary("lnf");

            assertNotNull(result);
            assertEquals(5L, result.getOpenIssues().getOpenTotal());

            verify(issueRepository, times(7)).count(any(Specification.class));
        }
    }
}