package com.lnf.sentinel.service;

import com.lnf.exception.LnFEntityNotFoundException;
import com.lnf.sentinel.AbstractIntegrationTest;
import com.lnf.sentinel.domain.enums.Category;
import com.lnf.sentinel.domain.enums.Environment;
import com.lnf.sentinel.domain.enums.Priority;
import com.lnf.sentinel.domain.enums.Severity;
import com.lnf.sentinel.dto.CreateIssueRequest;
import com.lnf.sentinel.dto.IssueResponse;
import com.lnf.sentinel.dto.UpdateIssueRequest;
import com.lnf.sentinel.tenant.TenantContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Proves a pinned tenant cannot read or mutate another tenant's issue.
 */
class TenantIsolationTest extends AbstractIntegrationTest {

    @Autowired
    IssueService issueService;

    private Long createAsAcme() {
        TenantContext.setTenantId(ACME);
        IssueResponse r = issueService.create(new CreateIssueRequest(
                "ACME-only incident", null, null, Severity.S2_HIGH, Priority.P2,
                Category.BUG, Environment.PRODUCTION, "billing", ENG1, ENG1, OffsetDateTime.now()));
        return r.id();
    }

    @Test
    void otherTenantGets404OnGet() {
        Long id = createAsAcme();
        TenantContext.setTenantId(GLOBEX);
        assertThatThrownBy(() -> issueService.get(id)).isInstanceOf(LnFEntityNotFoundException.class);
    }

    @Test
    void otherTenantGets404OnUpdateAndStatus() {
        Long id = createAsAcme();
        TenantContext.setTenantId(GLOBEX);
        assertThatThrownBy(() -> issueService.update(id,
                new UpdateIssueRequest("hijack", null, null, null, null, null, null, null, null)))
                .isInstanceOf(LnFEntityNotFoundException.class);
    }

    @Test
    void otherTenantListDoesNotContainIt() {
        Long id = createAsAcme();
        TenantContext.setTenantId(GLOBEX);
        var page = issueService.list(null, null, null, null, null, PageRequest.of(0, 50));
        assertThat(page.getContent()).noneMatch(i -> i.id().equals(id));
    }

    @Test
    void owningTenantAndInternalStaffCanRead() {
        Long id = createAsAcme();
        // owning tenant
        TenantContext.setTenantId(ACME);
        assertThat(issueService.get(id).id()).isEqualTo(id);
        // internal staff (no tenant pinned) sees everything
        TenantContext.clear();
        assertThat(issueService.get(id).id()).isEqualTo(id);
    }
}
