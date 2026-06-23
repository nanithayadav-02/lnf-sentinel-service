package com.lnf.sentinel.converter;



import com.lnf.dto.sentinel.TenantEnvironmentDto;


import com.lnf.sentinel.model.enums.Environment;
import com.lnf.sentinel.model.TenantEnvironment;

import com.lnf.sentinel.model.TenantEnvironment;
import com.lnf.sentinel.model.enums.Environment;


public class TenantEnvironmentConverter {

    private TenantEnvironmentConverter() {
    }


    public static TenantEnvironmentDto toTransportModel(TenantEnvironment entity) {

        if (entity == null) {
            return null;
        }

        TenantEnvironmentDto dto = new TenantEnvironmentDto();
        dto.setTenantId(
                entity.getTenantId() != null
                        ? java.util.UUID.fromString(entity.getTenantId().toString())
                        : null
        );

        dto.setEnvironment(
                entity.getEnvironment() != null
                        ? String.valueOf(entity.getEnvironment())
                        : null
        );

        dto.setBaseUrl(entity.getBaseUrl());
        dto.setHealthCheckUrl(entity.getHealthCheckUrl());
        dto.setRegion(entity.getRegion());

        return dto;
    }


    public static TenantEnvironment toEntityModel(TenantEnvironmentDto dto, TenantEnvironment entity) {

        if (dto == null || entity == null) {
            return null;
        }


        entity.setTenantId(dto.getTenantId());

        entity.setEnvironment(
                dto.getEnvironment() != null
                        ? Environment.valueOf(dto.getEnvironment())
                        : null
        );

        entity.setBaseUrl(dto.getBaseUrl());
        entity.setHealthCheckUrl(dto.getHealthCheckUrl());
        entity.setRegion(dto.getRegion());

        return entity;
    }
}
