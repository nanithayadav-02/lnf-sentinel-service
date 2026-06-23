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
            throw new LnFBadRequestException("Tenant code already exists: " + req.getTenantCode());
        }
        Tenant t = new Tenant();
        t.setTenantCode(req.getTenantCode());
        t.setName(req.getName());
        t.setStatus(req.getStatus() != null ? TenantStatus.valueOf(req.getStatus()): TenantStatus.ACTIVE);
        t.setSupportTier(req.getSupportTier() != null ?  SupportTier.valueOf(req.getSupportTier()) : SupportTier.STANDARD);
        t.setRegion(req.getRegion());
        t.setProductionUrl(req.getProductionUrl());
        t.setPrimaryContactName(req.getPrimaryContactName());
        t.setPrimaryContactEmail(req.getPrimaryContactEmail());
        return TenantConverter.toTransportModel(tenantRepository.save(t));
    }

    @Transactional(readOnly = true)
    public List<TenantDto> list() {
        return tenantRepository.findAll().stream().map(TenantConverter::toTransportModel).toList();
    }

    @Transactional(readOnly = true)
    public TenantDto get(UUID id) {
        return tenantRepository.findById(id)
                .map(TenantConverter::toTransportModel)
                .orElseThrow(() -> new LnFEntityNotFoundException("Tenant not found: " + id));
    }
}
