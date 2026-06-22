package com.lnf.sentinel.model;

import com.lnf.model.AuditableEntity;
import com.lnf.sentinel.model.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * The core record: one production issue belonging to one tenant.
 * This IS the engineering record of truth: triage, fix and resolution live here.
 */
@Entity
@Table(name = "issues")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Issue extends AuditableEntity {

    @Column(name = "issue_key", nullable = false, unique = true, length = 20)
    private String issueKey;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false, length = 240)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Severity severity = Severity.S3_MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Priority priority = Priority.P3;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IssueStatus status = IssueStatus.NEW;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Category category = Category.BUG;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Environment environment = Environment.PRODUCTION;

    @Column(name = "affected_service", length = 120)
    private String affectedService;

    @Column(name = "reported_by")
    private Long reportedBy;

    @Column(name = "assignee_id")
    private UUID assigneeId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Resolution resolution;

    @Column(name = "root_cause", columnDefinition = "text")
    private String rootCause;

    @Column(name = "fix_version", length = 60)
    private String fixVersion;

    @Column(name = "detected_at")
    private OffsetDateTime detectedAt;

    @Column(name = "sla_due_at")
    private OffsetDateTime slaDueAt;

    @Column(name = "resolved_at")
    private OffsetDateTime resolvedAt;

    /** True when past SLA and not yet in a terminal state. */
    @Transient
    public boolean isSlaBreached() {
        if (slaDueAt == null) {
            return false;
        }
        if (status == IssueStatus.RESOLVED || status == IssueStatus.CLOSED) {
            return false;
        }
        return OffsetDateTime.now().isAfter(slaDueAt);
    }
}
