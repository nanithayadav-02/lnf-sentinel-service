package com.lnf.sentinel.domain;

import com.lnf.sentinel.domain.enums.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

/**
 * The core record: one production issue belonging to one tenant.
 * This IS the engineering record of truth: triage, fix and resolution live here.
 */
@Entity
@Table(name = "issues")
@Getter
@Setter
@NoArgsConstructor
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "issue_key", nullable = false, unique = true, length = 20)
    private String issueKey;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

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
    private Long assigneeId;

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

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

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
