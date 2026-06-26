package com.lnf.sentinel.service;

import com.lnf.dto.sentinel.IssueCommentDto;
import com.lnf.exception.LnFEntityNotFoundException;
import com.lnf.sentinel.converter.IssueCommentConverter;
import com.lnf.sentinel.model.Issue;
import com.lnf.sentinel.model.IssueComment;
import com.lnf.sentinel.repository.IssueCommentRepository;
import com.lnf.sentinel.repository.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IssueCommentService {

    private final IssueCommentRepository repository;
    private final IssueRepository issueRepository;


    public void addComment(UUID issueId, IssueCommentDto resource) {
       Issue issue= searchForIssueId(issueId);
       IssueComment comment= IssueCommentConverter.toEntityModel(new IssueComment(), resource);
      comment.setIssueId(issueId);
        repository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<IssueCommentDto> listComments(UUID issueId) {
        searchForIssueId(issueId);
        return repository.findByIssueId(issueId)
                .stream().map(IssueCommentConverter::toTransportModel).toList();
    }

    private Issue searchForIssueId(UUID id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new LnFEntityNotFoundException("Issue not found: " + id));
    }

}
