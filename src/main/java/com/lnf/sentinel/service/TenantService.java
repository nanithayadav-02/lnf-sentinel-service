package com.lnf.sentinel.service;

import com.lnf.exception.LnFBadRequestException;
import com.lnf.exception.LnFEntityNotFoundException;
import com.lnf.sentinel.model.Tenant;
import com.lnf.sentinel.model.enums.SupportTier;
import com.lnf.sentinel.model.enums.TenantStatus;
import com.lnf.sentinel.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/*
@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    @Transactional
    public TenantResponse create(CreateTenantRequest req) {
        if (tenantRepository.existsByTenantCode(req.tenantCode())) {
            throw new LnFBadRequestException("Tenant code already exists: " + req.tenantCode());
        }
        Tenant t = new Tenant();
        t.setTenantCode(req.tenantCode());
        t.setName(req.name());
        t.setStatus(req.status() != null ? req.status() : TenantStatus.ACTIVE);
        t.setSupportTier(req.supportTier() != null ? req.supportTier() : SupportTier.STANDARD);
        t.setRegion(req.region());
        t.setProductionUrl(req.productionUrl());
        t.setPrimaryContactName(req.primaryContactName());
        t.setPrimaryContactEmail(req.primaryContactEmail());
        return TenantResponse.from(tenantRepository.save(t));
    }

    @Transactional(readOnly = true)
    public List<TenantResponse> list() {
        return tenantRepository.findAll().stream().map(TenantResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public TenantResponse get(Long id) {
        return tenantRepository.findById(id)
                .map(TenantResponse::from)
                .orElseThrow(() -> new LnFEntityNotFoundException("Tenant not found: " + id));
    }
}
*/