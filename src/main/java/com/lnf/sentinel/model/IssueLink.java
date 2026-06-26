package com.lnf.sentinel.model;

import com.lnf.model.AuditableEntity;
import com.lnf.sentinel.model.enums.LinkType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "issue_links")
@Getter
@Setter
@NoArgsConstructor
public class IssueLink extends AuditableEntity {

    @Column(name = "source_issue_id", nullable = false)
    private UUID sourceIssueId;

    @Column(name = "target_issue_id", nullable = false)
    private UUID targetIssueId;

    @Enumerated(EnumType.STRING)
    @Column(name = "link_type", nullable = false)
    private LinkType linkType;
}