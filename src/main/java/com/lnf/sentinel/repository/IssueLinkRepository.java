package com.lnf.sentinel.repository;

import com.lnf.sentinel.model.IssueLink;
import com.lnf.sentinel.model.enums.LinkType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface IssueLinkRepository extends JpaRepository<IssueLink, UUID> {
    List<IssueLink> findBySourceIssueId(UUID sourceIssueId);

    List<IssueLink> findByTargetIssueId(UUID targetIssueId);

    boolean existsBySourceIssueIdAndTargetIssueIdAndLinkType(UUID sourceIssueId, UUID targetIssueId, LinkType linkType);

    @Query("SELECT i.id,il.id,i.rootCause,i.issueKey,i.title,il.linkType FROM IssueLink il JOIN Issue i " +
            "ON i.id = il.targetIssueId WHERE il.sourceIssueId = :issueId")
    List<Object[]> findByIssuesDetails(@Param(value = "issueId") UUID issueId);

}
