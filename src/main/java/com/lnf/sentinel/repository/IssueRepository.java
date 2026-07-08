package com.lnf.sentinel.repository;

import com.lnf.sentinel.model.Issue;
import com.lnf.sentinel.model.enums.IssueStatus;
import com.lnf.sentinel.model.enums.Severity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IssueRepository extends JpaRepository<Issue, UUID>, JpaSpecificationExecutor<Issue> {

    Optional<Issue> findByIssueKey(String issueKey);

    Optional<Issue> findById(UUID issueId);

    @Query("SELECT COUNT(i) FROM Issue i " +
            "WHERE i.status NOT IN :statuses " +
            "AND DATE(i.createdTime) >= :issueStartDate AND DATE(i.createdTime) < :issueEndDate " +
            "AND (:tenantName IS NULL OR i.tenantName = :tenantName)")
    long countIssuesByStatusNotInAndDateAndTenantName(@Param("statuses") List<IssueStatus> statuses,
                                                      @Param("issueStartDate") LocalDate issueStartDate,
                                                      @Param("issueEndDate") LocalDate issueEndDate,
                                                      @Param("tenantName") String tenantName);

    @Query("SELECT COUNT(DISTINCT i.tenantName) FROM Issue i WHERE i.severity = :severity And " +
            "(:tenantName IS NULL OR i.tenantName = :tenantName)")
    long countDistinctTenantsBySeverityAndTenantName(@Param("severity") Severity severity,
                                                     @Param("tenantName") String tenantName);

    /**
     * Next value of the issue-key sequence (see V2 migration).
     */
    @Query(value = "SELECT nextval('public.issue_key_seq')", nativeQuery = true)
    long nextIssueKeyNumber();

    @Query(value = "SELECT s.severity,COUNT(i.severity) FROM(VALUES ('S1_CRITICAL'), ('S2_HIGH'), ('S3_MEDIUM')," +
            "('S4_LOW')) AS s(severity) " +
            "LEFT JOIN issues i ON i.severity = s.severity AND (:tenantName IS NULL OR i.tenant_name = :tenantName) " +
            " GROUP BY s.severity" +
            " UNION ALL " +
            "SELECT v.status, COUNT(i.status) " +
            "FROM (VALUES ('IN_PROGRESS'), ('AWAITING_TENANT'), ('RESOLVED')) AS v(status) " +
            "LEFT JOIN issues i ON i.status = v.status AND (:tenantName IS NULL OR i.tenant_name = :tenantName) " +
            "GROUP BY v.status", nativeQuery = true)
    List<Object[]> getIssueCountByseverity(@Param(value = "tenantName") String tenantName);

}
