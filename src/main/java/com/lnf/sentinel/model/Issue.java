package com.lnf.sentinel.model;

import com.lnf.model.AuditableEntity;
import com.lnf.sentinel.model.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "issues")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Issue extends AuditableEntity {

    @Column(name = "issue_key", nullable = false, unique = true)
    private String issueKey;

    @Column(name = "title", nullable = false)
    private String title;

    private String description;

    @Column(name = "tenant_name")
    private String tenantName;

    @Column(name = "root_cause")
    private String rootCause;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueStatus status = IssueStatus.NEW;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity = Severity.S3_MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.P3;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category = Category.BUG;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Environment environment = Environment.PRODUCTION;

    @Column(name = "assignee_user_name")
    private String assigneeUserName;

    @Column(name = "assignee_email")
    private String assigneeEmail;

    @Column(name = "affected_service")
    private String affectedService;

    private String resolution;

    @Column(name = "detected_at")
    private Date detectedAt;

    @Column(name = "sla_due_at")
    private Date slaDueAt;

    @Column(name = "resolved_at")
    private Date resolvedAt;

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL)
    private List<IssueAuditHistory> auditHistories;

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
