package com.lnf.sentinel.model;

import com.lnf.model.AuditableEntity;
import com.lnf.sentinel.model.enums.IssueStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "issue_status_history")
@Getter
@Setter
@NoArgsConstructor
public class IssueStatusHistory extends AuditableEntity {

    @Column(name = "issue_id", nullable = false)
    private UUID issueId;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 20)
    private IssueStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", length = 20)
    private IssueStatus toStatus;

    @Column(name = "changed_by")
    private String changedBy;

    @Column(name = "note", length = 500)
    private String notes;

}
