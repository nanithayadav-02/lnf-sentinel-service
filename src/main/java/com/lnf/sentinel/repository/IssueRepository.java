package com.lnf.sentinel.repository;

import com.lnf.sentinel.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IssueRepository extends JpaRepository<Issue, UUID>, JpaSpecificationExecutor<Issue> {

    Optional<Issue> findByIssueKey(String issueKey);

    Optional<Issue> findById(UUID issueId);

    /** Next value of the issue-key sequence (see V2 migration). */
    @Query(value = "SELECT nextval('public.issue_key_seq')", nativeQuery = true)
    long nextIssueKeyNumber();

    @Query(value ="SELECT s.severity,COUNT(i.severity) FROM(VALUES ('S1_CRITICAL'), ('S2_HIGH'), ('S3_MEDIUM')," +
            "('S4_LOW')) AS s(severity) " +
            "LEFT JOIN issues i ON i.severity = s.severity AND (:tenantName IS NULL OR i.tenant_name = :tenantName) " +
            " GROUP BY s.severity" +
            " UNION ALL " +
            "SELECT v.status, COUNT(i.status) " +
            "FROM (VALUES ('IN_PROGRESS'), ('AWAITING_TENANT'), ('RESOLVED')) AS v(status) " +
            "LEFT JOIN issues i ON i.status = v.status AND (:tenantId IS NULL OR i.tenant_name = :tenantName) " +
            "GROUP BY v.status",nativeQuery = true)
    List<Object[]> getIssueCountByseverity(@Param(value="tenantName") String tenantName);
}
