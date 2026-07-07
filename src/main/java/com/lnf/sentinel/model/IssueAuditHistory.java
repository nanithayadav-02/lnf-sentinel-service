package com.lnf.sentinel.model;
import com.fasterxml.jackson.databind.JsonNode;
import com.lnf.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @Column(nullable = false, length = 100)
    private String module;

    @Column(nullable = false, length = 50)
    private String action;

    @Column(name = "entity_id")
    private UUID entityId;

    @Column(name = "performed_by")
    private String performedBy;

    @Column
    private String details;

    @Column(name = "input_payload")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode inputPayload;

}
