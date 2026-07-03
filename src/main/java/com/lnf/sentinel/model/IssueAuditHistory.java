package com.lnf.sentinel.model;
import com.lnf.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "issue_audit_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueAuditHistory extends AuditableEntity {

    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "issue_code", nullable = false)
    private String issueCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;

}
