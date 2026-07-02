package com.lnf.sentinel.repository;

import com.lnf.sentinel.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IssueRepository extends JpaRepository<Issue, UUID>, JpaSpecificationExecutor<Issue> {

    Optional<Issue> findByIssueKey(String issueKey);

    Optional<Issue> findById(UUID issueId);

    /** Next value of the issue-key sequence (see V2 migration). */
    @Query(value = "SELECT nextval('public.issue_key_seq')", nativeQuery = true)
    long nextIssueKeyNumber();

    @Query(value ="SELECT severity,COUNT(*) FROM issues GROUP BY severity" +
            " UNION ALL " +
            "SELECT status,COUNT(*) FROM issues where status <> 'CLOSED' GROUP BY status",nativeQuery = true)
    List<Object[]> getIssueCountByseverity();
}
