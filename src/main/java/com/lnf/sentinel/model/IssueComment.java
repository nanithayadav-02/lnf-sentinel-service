package com.lnf.sentinel.model;

import com.lnf.model.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "issue_comments")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IssueComment extends AuditableEntity {

    @Column(name = "issue_id", nullable = false)
    private UUID issueId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "tenant_name")
    private String tenantName;

    @Column(nullable = false, columnDefinition = "text")
    private String body;

    @Column(nullable = false)
    private boolean internal = true;

    @Column(name="author_id")
    private UUID authorId;
}
