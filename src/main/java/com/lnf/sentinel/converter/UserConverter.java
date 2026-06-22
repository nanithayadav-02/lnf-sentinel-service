package com.lnf.sentinel.converter;

import com.lnf.dto.sentinel.UserDto;
import com.lnf.sentinel.domain.User;
import com.lnf.sentinel.domain.enums.UserRole;

public class UserConverter {

    private UserConverter() {
    }


    public static UserDto toTransportModel(User entity) {

        if (entity == null) {
            return null;
        }

        UserDto dto = new UserDto();

        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setFullName(entity.getFullName());

        dto.setRole(
                entity.getRole() != null
                        ? String.valueOf(entity.getRole())
                        : null
        );

        dto.setTenantId(
                entity.getTenantId() != null
                        ? java.util.UUID.fromString(entity.getTenantId().toString())
                        : null
        );

        dto.setActive(entity.isActive());

        return dto;
    }


    public static User toEntityModel(UserDto dto, User entity) {

        if (dto == null || entity == null) {
            return null;
        }

        entity.setEmail(dto.getEmail());
        entity.setFullName(dto.getFullName());

        entity.setRole(
                dto.getRole() != null
                        ? UserRole.valueOf(dto.getRole())
                        : null
        );

        entity.setTenantId(dto.getTenantId());

        entity.setActive(dto.isActive());

        return entity;
    }
}