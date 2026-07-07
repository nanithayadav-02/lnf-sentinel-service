package com.lnf.sentinel.repository;

import com.lnf.sentinel.model.IssueWatcher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IssueWatcherRepository extends JpaRepository<IssueWatcher, UUID> {

    List<IssueWatcher> findByIssueId(UUID issueId);

    Optional<IssueWatcher> findByIssueIdAndUserEmail(UUID issueId, String userEmail);

}
