package com.lnf.sentinel.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "issue_comments")
@Getter
@Setter
@NoArgsConstructor
public class IssueComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "issue_id", nullable = false)
    private Long issueId;

    @Column(name = "author_id")
    private Long authorId;

    @Column(nullable = false, columnDefinition = "text")
    private String body;

    /** TRUE = ops-only note (not tenant-visible). */
    @Column(nullable = false)
    private boolean internal = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
