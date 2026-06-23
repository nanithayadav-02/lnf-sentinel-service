package com.lnf.sentinel.repository;

import com.lnf.sentinel.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    Optional<Tenant> findByTenantCode(String tenantCode);
    boolean existsByTenantCode(String tenantCode);

<<<<<<< HEAD
    Optional<Tenant>findById(UUID tenantId);
=======
    boolean existsById(UUID tenantId);
>>>>>>> c349a78efc589e05e7eb1669f6b55d2017282f40
}
