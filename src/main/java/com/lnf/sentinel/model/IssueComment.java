package com.lnf.sentinel.model;

import com.lnf.model.AuditableEntity;
import jakarta.persistence.*;
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

    @Column(name = "author_id")
    private UUID authorId;

    @Column(nullable = false, columnDefinition = "text")
    private String body;

    /** TRUE = ops-only note (not tenant-visible). */
    @Column(nullable = false)
    private boolean internal = true;
}
