package com.lnf.sentinel.service;

import com.lnf.dto.sentinel.TenantDto;
import com.lnf.exception.LnFBadRequestException;
import com.lnf.exception.LnFEntityNotFoundException;
import com.lnf.sentinel.model.Tenant;
import com.lnf.sentinel.model.enums.SupportTier;
import com.lnf.sentinel.model.enums.TenantStatus;
import com.lnf.sentinel.repository.TenantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private TenantService tenantService;

    @Test
    void shouldCreateTenantSuccessfully() {

        TenantDto request = new TenantDto();
        request.setTenantCode("TENANT001");
        request.setName("Acme Corp");
        request.setStatus("ACTIVE");
        request.setSupportTier("STANDARD");

        Tenant savedTenant = new Tenant();
        savedTenant.setId(UUID.randomUUID());
        savedTenant.setTenantCode("TENANT001");
        savedTenant.setName("Acme Corp");
        savedTenant.setStatus(TenantStatus.ACTIVE);
        savedTenant.setSupportTier(SupportTier.STANDARD);

        when(tenantRepository.existsByTenantCode("TENANT001")).thenReturn(false);
        when(tenantRepository.save(any(Tenant.class))).thenReturn(savedTenant);

        TenantDto result = tenantService.create(request);

        assertNotNull(result);
        assertEquals("TENANT001", result.getTenantCode());

        verify(tenantRepository).existsByTenantCode("TENANT001");
        verify(tenantRepository).save(any(Tenant.class));
    }



    @Test
    void shouldThrowExceptionWhenTenantCodeAlreadyExists() {

        TenantDto request = new TenantDto();
        request.setTenantCode("TENANT001");

        when(tenantRepository.existsByTenantCode("TENANT001")).thenReturn(true);

        assertThrows(LnFBadRequestException.class, () -> {
            tenantService.create(request);
        });

        verify(tenantRepository).existsByTenantCode("TENANT001");
        verify(tenantRepository, never()).save(any());
    }


    @Test
    void shouldReturnAllTenants() {

        Tenant tenant = new Tenant();
        tenant.setTenantCode("TENANT001");
        tenant.setName("Acme Corp");

        when(tenantRepository.findAll()).thenReturn(List.of(tenant));

        List<TenantDto> result = tenantService.list();

        assertEquals(1, result.size());
        assertEquals("TENANT001", result.get(0).getTenantCode());

        verify(tenantRepository).findAll();
    }



    @Test
    void shouldReturnTenantById() {

        UUID id = UUID.randomUUID();

        Tenant tenant = new Tenant();
        tenant.setTenantCode("TENANT001");
        tenant.setName("Acme Corp");

        when(tenantRepository.findById(id)).thenReturn(Optional.of(tenant));

        TenantDto result = tenantService.get(id);

        assertNotNull(result);
        assertEquals("TENANT001", result.getTenantCode());

        verify(tenantRepository).findById(id);
    }



    @Test
    void shouldThrowExceptionWhenTenantNotFound() {

        UUID id = UUID.randomUUID();

        when(tenantRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(LnFEntityNotFoundException.class, () -> {
            tenantService.get(id);
        });

        verify(tenantRepository).findById(id);
    }

}