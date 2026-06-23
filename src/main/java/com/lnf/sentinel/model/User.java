package com.lnf.sentinel.model;

import com.lnf.model.AuditableEntity;
import com.lnf.sentinel.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends AuditableEntity {

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(name = "full_name", nullable = false, length = 160)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.ENGINEER;

    @Column(name = "tenant_name")
    private String tenantName; //for external one

    @Column(nullable = false)
    private boolean active = true;

}
