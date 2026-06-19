package com.lnf.sentinel.repository;

import com.lnf.sentinel.domain.IssueWatcher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IssueWatcherRepository extends JpaRepository<IssueWatcher, Long> {
    List<IssueWatcher> findByIssueId(Long issueId);
    Optional<IssueWatcher> findByIssueIdAndUserId(Long issueId, Long userId);
    boolean existsByIssueIdAndUserId(Long issueId, Long userId);
}
