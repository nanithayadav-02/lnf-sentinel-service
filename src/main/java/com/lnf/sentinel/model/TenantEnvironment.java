package com.lnf.sentinel.model;

import com.lnf.model.AuditableEntity;
import com.lnf.sentinel.model.enums.Environment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

import java.util.UUID;

@Entity
@Table(name = "tenant_environments")
@Getter
@Setter
@NoArgsConstructor
public class TenantEnvironment extends AuditableEntity {

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Environment environment;

    @Column(name = "base_url")
    private String baseUrl;

    @Column(name = "health_check_url")
    private String healthCheckUrl;

    @Column(length = 40)
    private String region;
}
