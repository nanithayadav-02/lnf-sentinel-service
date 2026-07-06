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

import java.util.*;

@Service
@RequiredArgsConstructor
public class IssueCommentService {

    private final IssueCommentRepository repository;
    private final IssueRepository issueRepository;


    public void addComment(UUID issueId, IssueCommentDto resource) {
        Issue issue = searchForIssueId(issueId);
        IssueComment comment = IssueCommentConverter.toEntityModel(new IssueComment(), resource);
        comment.setIssueId(issueId);
        repository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<Map<String,Object>> listComments(UUID issueId) {
        searchForIssueId(issueId);
        List<Object[]> comment=repository.findByIssueId(issueId);
      List<Map<String,Object>> result=new ArrayList<>();

      for(Object[] values:comment){
          Map<String,Object> map=new HashMap<>();
          map.put("IssueId",values[0]);
          map.put("UserId",values[1]);
          map.put("FullName",values[2]);
          map.put("createdTime",values[3]);
          result.add(map);
      }
     return result;

}

    private Issue searchForIssueId(UUID id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new LnFEntityNotFoundException("Issue not found: " + id));
    }

}
