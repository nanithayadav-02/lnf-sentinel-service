package com.lnf.sentinel.converter;



import com.lnf.dto.sentinel.TenantDto;
import com.lnf.sentinel.domain.Tenant;
import com.lnf.sentinel.domain.enums.SupportTier;
import com.lnf.sentinel.domain.enums.TenantStatus;

public class TenantConverter {

    private TenantConverter() {
    }


    public static TenantDto toTransportModel(Tenant entity) {

        if (entity== null) {
            return null;
        }

        TenantDto dto = new TenantDto();

        dto.setId(entity.getId());
        dto.setTenantCode(entity.getTenantCode());
        dto.setName(entity.getName());

        dto.setStatus(
                entity.getStatus() != null
                        ? String.valueOf(entity.getStatus())
                        : null
        );

        dto.setSupportTier(
                entity.getSupportTier() != null
                        ? String.valueOf(entity.getSupportTier())
                        : null
        );

        dto.setRegion(entity.getRegion());
        dto.setProductionUrl(entity.getProductionUrl());
        dto.setPrimaryContactName(entity.getPrimaryContactName());
        dto.setPrimaryContactEmail(entity.getPrimaryContactEmail());

        return dto;
    }


    public static Tenant toEntityModel(TenantDto dto, Tenant entity) {

        if (dto == null || entity == null) {
            return null;
        }

        entity.setTenantCode(dto.getTenantCode());
        entity.setName(dto.getName());

        entity.setStatus(
                dto.getStatus() != null
                        ? TenantStatus.valueOf(dto.getStatus())
                        : null
        );

        entity.setSupportTier(
                dto.getSupportTier() != null
                        ? SupportTier.valueOf(dto.getSupportTier())
                        : null
        );

        entity.setRegion(dto.getRegion());
        entity.setProductionUrl(dto.getProductionUrl());
        entity.setPrimaryContactName(dto.getPrimaryContactName());
        entity.setPrimaryContactEmail(dto.getPrimaryContactEmail());

        return entity;
    }
}
