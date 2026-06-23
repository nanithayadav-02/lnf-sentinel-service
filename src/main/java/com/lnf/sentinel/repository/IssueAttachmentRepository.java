package com.lnf.sentinel.repository;

import com.lnf.sentinel.model.IssueAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IssueAttachmentRepository extends JpaRepository<IssueAttachment, UUID> {
    List<IssueAttachment> findByIssueId(UUID issueId);
}
