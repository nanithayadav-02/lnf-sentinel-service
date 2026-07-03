package com.lnf.sentinel.repository;

import com.lnf.sentinel.model.Issue;
import com.lnf.sentinel.model.IssueComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IssueCommentRepository extends JpaRepository<IssueComment, UUID> {

    @Query("select i.issueId,i.userId,u.fullName,i.createdTime from IssueComment i JOIN User u ON i.userId=u.id where i.issueId=:issueId")
    List<Object[]> findByIssueId(@Param("issueId") UUID issueId);

    Optional<IssueComment> findById(UUID issueId);
}
