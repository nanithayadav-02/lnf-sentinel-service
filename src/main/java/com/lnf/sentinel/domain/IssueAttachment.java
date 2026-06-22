package com.lnf.sentinel.domain;

import com.lnf.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "issue_attachments")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IssueAttachment extends AuditableEntity {

    @Column(name = "issue_id", nullable = false)
    private UUID issueId;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "storage_key", nullable = false, length = 512)
    private String storageKey;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Column(name = "uploaded_by")
    private Long uploadedBy;

}
