package com.lnf.sentinel.repository;

import com.lnf.sentinel.model.TenantEnvironment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TenantEnvironmentRepository extends JpaRepository<TenantEnvironment, UUID> {
    List<TenantEnvironment> findByTenantId(UUID tenantId);
}
