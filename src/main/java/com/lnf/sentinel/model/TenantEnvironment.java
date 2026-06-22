package com.lnf.sentinel.model;

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
public class TenantEnvironment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Environment environment;

    @Column(name = "base_url", length = 255)
    private String baseUrl;

    @Column(name = "health_check_url", length = 255)
    private String healthCheckUrl;

    @Column(length = 40)
    private String region;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
