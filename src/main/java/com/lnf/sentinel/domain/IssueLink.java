package com.lnf.sentinel.domain;

import com.lnf.model.AuditableEntity;
import com.lnf.sentinel.domain.enums.LinkType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Column(name = "link_type", nullable = false, length = 20)
    private LinkType linkType;


}
