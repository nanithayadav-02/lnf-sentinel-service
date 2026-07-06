package com.lnf.sentinel.model;

import com.lnf.model.AuditableEntity;
import com.lnf.sentinel.model.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

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

    @Column(name = "summary", nullable = false)
    private String summary;

    private String description;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "tenant_name")
    private String tenantName;

    @Column(name="root_cause")
    private String rootCause;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IssueStatus status = IssueStatus.NEW;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Severity severity = Severity.S3_MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Priority priority = Priority.P3;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Category category = Category.BUG;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Environment environment = Environment.PRODUCTION;

    @Column(name = "assignee_id")
    private UUID assigneeId;

    @Column(name = "assignee_name")
    private String assignee;

    @Column(name = "affected_service", length = 120)
    private String affectedService;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Resolution resolution;

    @Column(name = "reported_by")
    private String  reportedBy;

    @Column(name = "detected_at")
    private Date detectedAt;

    @Column(name = "sla_due_at")
    private Date slaDueAt;

    @Column(name = "resolved_at")
    private Date resolvedAt;

    /**
     * True when past SLA and not yet in a terminal state.
     */
    @Transient
    public boolean isSlaBreached() {
        if (slaDueAt == null) {
            return false;
        }
        if (status == IssueStatus.RESOLVED || status == IssueStatus.CLOSED) {
            return false;
        }
        return new Date().after(slaDueAt);
    }

}
