package com.lnf.sentinel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lnf.dto.sentinel.TenantDto;
import com.lnf.sentinel.service.TenantService;
import com.lnf.sentinel.tenant.TenantFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TenantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TenantService tenantService;

    @MockBean
    private TenantFilter tenantFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnAllTenants() throws Exception {

        TenantDto dto = new TenantDto();
        dto.setTenantCode("TENANT001");
        dto.setName("Acme Corp");

        when(tenantService.list()).thenReturn(List.of(dto));

        mockMvc.perform(get("/sentinel/tenant"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tenantCode").value("TENANT001"));
    }
    @Test
    void shouldCreateTenant() throws Exception {

        TenantDto dto = new TenantDto();
        dto.setTenantCode("TENANT001");

        when(tenantService.create(any(TenantDto.class)))
                .thenReturn(dto);

        mockMvc.perform(post("/sentinel/tenant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }




    @Test
    void shouldReturnTenantById() throws Exception {

        UUID id = UUID.randomUUID();

        TenantDto dto = new TenantDto();
        dto.setTenantCode("TENANT001");

        when(tenantService.get(id)).thenReturn(dto);

        mockMvc.perform(get("/sentinel/tenant/" + id))
                .andExpect(status().isOk());
    }
}