package com.lnf.sentinel.repository;

import com.lnf.sentinel.domain.IssueStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueStatusHistoryRepository extends JpaRepository<IssueStatusHistory, Long> {
    List<IssueStatusHistory> findByIssueIdOrderByChangedAtAsc(Long issueId);
}
