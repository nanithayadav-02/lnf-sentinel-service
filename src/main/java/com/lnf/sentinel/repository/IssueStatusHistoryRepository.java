package com.lnf.sentinel.repository;

import com.lnf.sentinel.model.IssueStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IssueStatusHistoryRepository extends JpaRepository<IssueStatusHistory, UUID> {
    List<IssueStatusHistory> findByIssueIdOrderByChangedAtAsc(Long issueId);
}
