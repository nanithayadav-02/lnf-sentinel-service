package com.lnf.sentinel.repository;

import com.lnf.sentinel.domain.IssueLink;
import com.lnf.sentinel.domain.enums.LinkType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueLinkRepository extends JpaRepository<IssueLink, Long> {
    List<IssueLink> findBySourceIssueId(Long sourceIssueId);
    List<IssueLink> findByTargetIssueId(Long targetIssueId);
    boolean existsBySourceIssueIdAndTargetIssueIdAndLinkType(Long sourceIssueId, Long targetIssueId, LinkType linkType);
}
