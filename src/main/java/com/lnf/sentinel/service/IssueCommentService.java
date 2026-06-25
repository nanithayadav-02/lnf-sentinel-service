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


    @Transactional
    public IssueCommentDto addComment(UUID issueId, IssueCommentDto resource) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new LnFEntityNotFoundException("Issue not found: " + issueId));

        IssueComment comment = new IssueComment();

        IssueCommentConverter.toEntityModel(comment, resource);

        comment.setIssueId(issueId);

        IssueComment saved = repository.save(comment);

        return IssueCommentConverter.toTransportModel(saved);
    }

    @Transactional(readOnly = true)
    public List<IssueCommentDto> listComments(UUID issueId) {
        searchForIssueId(issueId);
        return repository.findByIssueId(issueId)
                .stream().map(IssueCommentConverter::toTransportModel).toList();
    }

    private void searchForIssueId(UUID id) {
        issueRepository.findById(id)
                .orElseThrow(() -> new LnFEntityNotFoundException("Issue not found: " + id));
    }

}
