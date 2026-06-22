package com.lnf.sentinel.domain;

import com.lnf.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "issue_watchers")
@Getter
@Setter
@NoArgsConstructor
public class IssueWatcher extends AuditableEntity {


    @Column(name = "issue_id", nullable = false)
    private UUID issueId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;


}
