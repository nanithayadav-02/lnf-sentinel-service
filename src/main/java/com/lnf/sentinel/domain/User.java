package com.lnf.sentinel.domain;

import com.lnf.model.AuditableEntity;
import com.lnf.sentinel.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User  extends AuditableEntity {



    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(name = "full_name", nullable = false, length = 160)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.ENGINEER;

    /** NULL = internal staff (cross-tenant). */
    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(nullable = false)
    private boolean active = true;


}
