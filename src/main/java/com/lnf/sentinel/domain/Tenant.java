package com.lnf.sentinel.domain;

import com.lnf.model.AuditableEntity;
import com.lnf.sentinel.domain.enums.SupportTier;
import com.lnf.sentinel.domain.enums.TenantStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tenants")
@Getter
@Setter
@NoArgsConstructor
public class Tenant  extends AuditableEntity {

    @Column(name = "tenant_code", nullable = false, unique = true, length = 40)
    private String tenantCode;

    @Column(nullable = false, length = 160)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TenantStatus status = TenantStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "support_tier", nullable = false, length = 20)
    private SupportTier supportTier = SupportTier.STANDARD;

    @Column(length = 40)
    private String region;

    @Column(name = "production_url", length = 255)
    private String productionUrl;

    @Column(name = "primary_contact_name", length = 160)
    private String primaryContactName;

    @Column(name = "primary_contact_email", length = 160)
    private String primaryContactEmail;

}
