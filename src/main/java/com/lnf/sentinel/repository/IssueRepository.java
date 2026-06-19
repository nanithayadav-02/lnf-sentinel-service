package com.lnf.sentinel.repository;

import com.lnf.sentinel.domain.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, Long>, JpaSpecificationExecutor<Issue> {

    Optional<Issue> findByIdAndTenantId(Long id, Long tenantId);

    Optional<Issue> findByIssueKey(String issueKey);

    /** Next value of the issue-key sequence (see V2 migration). */
    @Query(value = "SELECT nextval('issue_key_seq')", nativeQuery = true)
    long nextIssueKeyNumber();
}
