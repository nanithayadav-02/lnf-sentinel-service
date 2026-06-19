package com.lnf.sentinel.repository;

import com.lnf.sentinel.domain.TenantEnvironment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TenantEnvironmentRepository extends JpaRepository<TenantEnvironment, Long> {
    List<TenantEnvironment> findByTenantId(Long tenantId);
}
