package com.lnf.sentinel.repository;

import com.lnf.sentinel.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    Optional<Tenant> findByTenantCode(String tenantCode);

    boolean existsByTenantCode(String tenantCode);

    Optional<Tenant>findById(UUID tenantId);

    boolean existsById(UUID tenantId);

    Optional<Tenant> findByName(String header);

    @Query("Select t.tenantCode,t.name,count(i) from Tenant t JOIN Issue i ON t.id=i.tenantId GROUP BY t.name,t.tenantCode")
    List<Object[]> findIssuesByTennat();

}
