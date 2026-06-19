package com.lnf.sentinel.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lnf.sentinel.AbstractIntegrationTest;
import com.lnf.sentinel.tenant.TenantFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class IssueControllerTest extends AbstractIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private static final String CREATE_BODY = """
            {"title":"Checkout returns 500","description":"spike after deploy",
             "severity":"S1_CRITICAL","priority":"P1","category":"BUG",
             "environment":"PRODUCTION","affectedService":"checkout","reportedBy":2}
            """;

    private long createIssueForAcme() throws Exception {
        String json = mockMvc.perform(post("/api/v1/issues")
                        .header(TenantFilter.TENANT_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.issueKey", org.hamcrest.Matchers.startsWith("PRD-")))
                .andExpect(jsonPath("$.tenantId").value(1))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(json).get("id").asLong();
    }

    @Test
    void createThenReadAsSameTenantSucceeds() throws Exception {
        long id = createIssueForAcme();
        mockMvc.perform(get("/api/v1/issues/{id}", id).header(TenantFilter.TENANT_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value((int) id));
    }

    @Test
    void crossTenantReadReturns404() throws Exception {
        long id = createIssueForAcme();
        mockMvc.perform(get("/api/v1/issues/{id}", id).header(TenantFilter.TENANT_HEADER, "2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void validationFailureReturns400() throws Exception {
        mockMvc.perform(post("/api/v1/issues")
                        .header(TenantFilter.TENANT_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void resolveWithoutResolutionReturns400() throws Exception {
        long id = createIssueForAcme();
        mockMvc.perform(patch("/api/v1/issues/{id}/status", id)
                        .header(TenantFilter.TENANT_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"RESOLVED\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void metricsSummaryIsReachable() throws Exception {
        createIssueForAcme();
        String json = mockMvc.perform(get("/api/v1/metrics/summary").header(TenantFilter.TENANT_HEADER, "1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(objectMapper.readTree(json).get("openTotal").asLong()).isGreaterThanOrEqualTo(1);
    }
}
