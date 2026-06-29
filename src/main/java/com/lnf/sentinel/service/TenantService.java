package com.lnf.sentinel.service;

import com.lnf.dto.sentinel.TenantDto;
import com.lnf.exception.LnFBadRequestException;
import com.lnf.exception.LnFEntityNotFoundException;
import com.lnf.sentinel.converter.TenantConverter;
import com.lnf.sentinel.model.Tenant;
import com.lnf.sentinel.model.enums.SupportTier;
import com.lnf.sentinel.model.enums.TenantStatus;
import com.lnf.sentinel.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    @Transactional
    public TenantDto create(TenantDto req) {

        if (tenantRepository.existsByTenantCode(req.getTenantCode())) {
            throw new LnFBadRequestException(
                    "Tenant code already exists: " + req.getTenantCode()
            );
        }

        Tenant tenant = new Tenant();
        tenant.setTenantCode(req.getTenantCode());
        tenant.setName(req.getName());
        tenant.setStatus(
                req.getStatus() != null
                        ? TenantStatus.valueOf(req.getStatus())
                        : TenantStatus.ACTIVE
        );

        tenant.setSupportTier(
                req.getSupportTier() != null
                        ? SupportTier.valueOf(req.getSupportTier())
                        : SupportTier.STANDARD
        );

        tenant.setRegion(req.getRegion());
        tenant.setProductionUrl(req.getProductionUrl());
        tenant.setPrimaryContactName(req.getPrimaryContactName());
        tenant.setPrimaryContactEmail(req.getPrimaryContactEmail());

        return TenantConverter.toTransportModel(
                tenantRepository.save(tenant)
        );
    }

    @Transactional(readOnly = true)
    public List<TenantDto> list() {
        return tenantRepository.findAll()
                .stream()
                .map(TenantConverter::toTransportModel)
                .toList();
    }

    @Transactional(readOnly = true)
    public TenantDto get(UUID tenantId) {
        return tenantRepository.findById(tenantId)
                .map(TenantConverter::toTransportModel)
                .orElseThrow(() ->
                        new LnFEntityNotFoundException(
                                "Tenant not found: " + tenantId
                        ));
    }
}