package com.lnf.sentinel.service;

import com.lnf.dto.sentinel.IssueCommentDto;
import com.lnf.sentinel.model.Issue;
import com.lnf.sentinel.model.IssueComment;
import com.lnf.sentinel.repository.IssueCommentRepository;
import com.lnf.sentinel.repository.IssueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IssueCommentServiceTest {

    @InjectMocks
    private IssueCommentService issueCommentService;

    @Mock
    private IssueCommentRepository issueCommentRepository;

    @Mock
    private IssueRepository issueRepository;

    @Test
    void testListOfIssueComments(){

        UUID issueId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Issue issue = new Issue();
        issue.setId(issueId);

        Object[] row = new Object[]{
                issueId,
                userId,
                "John Doe",
                LocalDateTime.now()
        };

        List<Object[]> rows=new ArrayList<>();
        rows.add(row);
        when(issueRepository.findById(issueId))
                .thenReturn(Optional.of(issue));

        when(issueCommentRepository.findByIssueId(issueId))
                .thenReturn(rows);


        List<Map<String, Object>> result = issueCommentService.listComments(issueId);

        assertEquals(1, result.size());

        verify(issueRepository).findById(issueId);
        verify(issueCommentRepository).findByIssueId(issueId);
    }

    @Test
    void testAddComment() {
        UUID issueId = UUID.randomUUID();

        Issue issue = new Issue();
        issue.setId(issueId);

        IssueCommentDto dto = new IssueCommentDto();
        dto.setId(issue.getId());

        when(issueRepository.findById(issueId))
                .thenReturn(Optional.of(issue));

        issueCommentService.addComment(issueId, dto);

        ArgumentCaptor<IssueComment> captor =
                ArgumentCaptor.forClass(IssueComment.class);

        verify(issueCommentRepository).save(captor.capture());
        IssueComment savedComment = captor.getValue();
        assertEquals(issueId, savedComment.getIssueId());
    }

}
