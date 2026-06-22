package com.lnf.sentinel.domain;

import com.lnf.model.AuditableEntity;
import com.lnf.sentinel.domain.enums.Environment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tenant_environments")
@Getter
@Setter
@NoArgsConstructor
public class TenantEnvironment  extends AuditableEntity {



    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Environment environment;

    @Column(name = "base_url", length = 255)
    private String baseUrl;

    @Column(name = "health_check_url", length = 255)
    private String healthCheckUrl;

    @Column(length = 40)
    private String region;


}
