package com.lnf.sentinel.repository;

import com.lnf.sentinel.model.IssueComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IssueCommentRepository extends JpaRepository<IssueComment, UUID> {
    List<IssueComment> findByIssueId(UUID issueId);
}
