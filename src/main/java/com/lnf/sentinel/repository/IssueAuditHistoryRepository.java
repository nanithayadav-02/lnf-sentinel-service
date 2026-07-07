package com.lnf.sentinel.repository;

import com.lnf.sentinel.model.IssueAuditHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface IssueAuditHistoryRepository extends JpaRepository<IssueAuditHistory, UUID>, JpaSpecificationExecutor<IssueAuditHistory> {

    @Query(value = "select i.id,i.issue_key,ia.action,ia.details,ia.created_time from issue_audit_history ia " +
            "JOIN issues i ON ia.entity_id=i.id ORDER BY ia.created_time DESC", nativeQuery = true)
    List<Object[]> findIssueAuditHistory();

}