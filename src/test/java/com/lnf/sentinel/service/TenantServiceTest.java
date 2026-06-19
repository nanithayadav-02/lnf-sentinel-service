package com.lnf.sentinel.service;

import com.lnf.exception.LnFBadRequestException;
import com.lnf.sentinel.AbstractIntegrationTest;
import com.lnf.sentinel.domain.enums.SupportTier;
import com.lnf.sentinel.domain.enums.TenantStatus;
import com.lnf.sentinel.dto.CreateTenantRequest;
import com.lnf.sentinel.dto.TenantResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TenantServiceTest extends AbstractIntegrationTest {

    @Autowired
    TenantService tenantService;

    @Test
    void listsSeededTenants() {
        assertThat(tenantService.list()).extracting(TenantResponse::tenantCode)
                .contains("ACME", "GLOBEX", "INITECH");
    }

    @Test
    void createsAndFetchesTenant() {
        TenantResponse created = tenantService.create(new CreateTenantRequest(
                "NEWCO", "New Company", TenantStatus.ONBOARDING, SupportTier.PREMIUM,
                "us-west-2", "https://newco.example.com", "Jess Doe", "jess@newco.example.com"));
        assertThat(created.id()).isNotNull();
        assertThat(tenantService.get(created.id()).tenantCode()).isEqualTo("NEWCO");
    }

    @Test
    void rejectsDuplicateTenantCode() {
        assertThatThrownBy(() -> tenantService.create(new CreateTenantRequest(
                "ACME", "Dup", null, null, null, null, null, null)))
                .isInstanceOf(LnFBadRequestException.class);
    }
}
