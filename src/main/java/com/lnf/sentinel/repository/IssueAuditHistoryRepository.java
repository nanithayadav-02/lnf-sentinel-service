package com.lnf.sentinel.repository;

import com.lnf.sentinel.model.IssueAuditHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface IssueAuditHistoryRepository extends JpaRepository<IssueAuditHistory, UUID>, JpaSpecificationExecutor<IssueAuditHistory> {

    List<IssueAuditHistory> findByIssueId(UUID issueId);
}