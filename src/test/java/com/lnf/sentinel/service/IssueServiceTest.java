package com.lnf.sentinel.service;

import com.lnf.exception.LnFBadRequestException;
import com.lnf.sentinel.AbstractIntegrationTest;
import com.lnf.sentinel.domain.enums.*;
import com.lnf.sentinel.dto.ChangeStatusRequest;
import com.lnf.sentinel.dto.CreateIssueRequest;
import com.lnf.sentinel.dto.IssueResponse;
import com.lnf.sentinel.repository.IssueStatusHistoryRepository;
import com.lnf.sentinel.tenant.TenantContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IssueServiceTest extends AbstractIntegrationTest {

    @Autowired IssueService issueService;
    @Autowired IssueStatusHistoryRepository historyRepository;

    @BeforeEach
    void pinAcme() {
        TenantContext.setTenantId(ACME);
    }

    private IssueResponse create(Severity sev) {
        return issueService.create(new CreateIssueRequest(
                "Orders API latency", "p99 over 2s", null, sev, Priority.P1,
                Category.PERFORMANCE, Environment.PRODUCTION, "orders-api", ENG1, ENG1,
                OffsetDateTime.now()));
    }

    @Test
    void createGeneratesKeySetsSlaAndInitialHistory() {
        IssueResponse r = create(Severity.S1_CRITICAL);

        assertThat(r.issueKey()).startsWith("PRD-");
        assertThat(r.tenantId()).isEqualTo(ACME);
        assertThat(r.status()).isEqualTo(IssueStatus.NEW);
        // S1 target is 4h from detection
        long hours = ChronoUnit.HOURS.between(r.detectedAt(), r.slaDueAt());
        assertThat(hours).isEqualTo(4);
        assertThat(historyRepository.findByIssueIdOrderByChangedAtAsc(r.id()))
                .hasSize(1)
                .allMatch(h -> h.getToStatus() == IssueStatus.NEW);
    }

    @Test
    void createRequiresTenantWhenNotPinned() {
        TenantContext.clear();
        assertThatThrownBy(() -> issueService.create(new CreateIssueRequest(
                "x", null, null, null, null, null, null, null, null, null, null)))
                .isInstanceOf(LnFBadRequestException.class);
    }

    @Test
    void resolveRequiresResolution() {
        IssueResponse r = create(Severity.S2_HIGH);
        assertThatThrownBy(() -> issueService.changeStatus(r.id(),
                new ChangeStatusRequest(IssueStatus.RESOLVED, null, null, null, "done", ENG1)))
                .isInstanceOf(LnFBadRequestException.class);
    }

    @Test
    void resolveThenReopenManagesResolutionFields() {
        IssueResponse r = create(Severity.S2_HIGH);

        IssueResponse resolved = issueService.changeStatus(r.id(),
                new ChangeStatusRequest(IssueStatus.RESOLVED, Resolution.FIXED,
                        "null pointer in mapper", "1.4.2", "fixed in hotfix", ENG1));
        assertThat(resolved.status()).isEqualTo(IssueStatus.RESOLVED);
        assertThat(resolved.resolution()).isEqualTo(Resolution.FIXED);
        assertThat(resolved.resolvedAt()).isNotNull();
        assertThat(resolved.fixVersion()).isEqualTo("1.4.2");

        IssueResponse reopened = issueService.changeStatus(r.id(),
                new ChangeStatusRequest(IssueStatus.REOPENED, null, null, null, "regressed", ENG1));
        assertThat(reopened.status()).isEqualTo(IssueStatus.REOPENED);
        assertThat(reopened.resolvedAt()).isNull();
        assertThat(reopened.resolution()).isNull();

        // create + resolve + reopen = 3 history rows
        assertThat(historyRepository.findByIssueIdOrderByChangedAtAsc(r.id())).hasSize(3);
    }

    @Test
    void listIsScopedAndFiltered() {
        IssueResponse s1 = create(Severity.S1_CRITICAL);
        create(Severity.S4_LOW);

        var page = issueService.list(null, null, Severity.S1_CRITICAL, null, null,
                org.springframework.data.domain.PageRequest.of(0, 20));
        assertThat(page.getContent()).extracting(IssueResponse::id).contains(s1.id());
        assertThat(page.getContent()).allMatch(i -> i.tenantId().equals(ACME));
    }
}
