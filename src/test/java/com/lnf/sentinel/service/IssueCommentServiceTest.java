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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.Assert.assertEquals;
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

        UUID uuid = UUID.randomUUID();
        Issue issue = new Issue();
        issue.setId(uuid);

        IssueComment issueComment = new IssueComment();
        issueComment.setId(UUID.randomUUID());
        issueComment.setIssueId(uuid);

        when(issueRepository.findById(uuid))
                .thenReturn(Optional.of(issue));

        when(issueCommentRepository.findByIssueId(uuid))
                .thenReturn(List.of(issueComment));

        List<IssueCommentDto> result = issueCommentService.listComments(uuid);

        assertEquals(1, result.size());

        verify(issueRepository).findById(uuid);
        verify(issueCommentRepository).findByIssueId(uuid);
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
