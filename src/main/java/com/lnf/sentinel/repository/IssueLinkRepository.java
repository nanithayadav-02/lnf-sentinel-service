package com.lnf.sentinel.repository;

import com.lnf.sentinel.model.IssueLink;
import com.lnf.sentinel.model.enums.LinkType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IssueLinkRepository extends JpaRepository<IssueLink, UUID> {
    List<IssueLink> findBySourceIssueId(UUID sourceIssueId);
    List<IssueLink> findByTargetIssueId(UUID targetIssueId);
    boolean existsBySourceIssueIdAndTargetIssueIdAndLinkType(Long sourceIssueId, Long targetIssueId, LinkType linkType);
}
