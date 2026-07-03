package com.lnf.sentinel.service;


import com.lnf.dto.sentinel.IssueDto;
import com.lnf.sentinel.converter.IssueConverter;
import com.lnf.sentinel.model.Issue;
import com.lnf.sentinel.model.IssueStatusHistory;
import com.lnf.sentinel.model.enums.IssueStatus;
import com.lnf.sentinel.model.enums.Severity;
import com.lnf.sentinel.repository.IssueRepository;
import com.lnf.sentinel.repository.IssueStatusHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.sql.Date;
import java.util.*;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IssueServiceTest {

    @InjectMocks
    private IssueService service;

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private IssueStatusHistoryRepository historyRepository;

    @Test
    void testCreateIssue() {
        IssueDto dto = new IssueDto();
        dto.setTenantId(UUID.randomUUID());
        dto.setTenantName("Tenant 1");
        dto.setSummary("Issue Summary");
        dto.setDescription("Issue Description");
        dto.setAssigneeId(UUID.randomUUID());
        dto.setAssignee("John");
        dto.setReportedBy("Siva");
        dto.setAffectedService("Service A");
        dto.setSeverity(String.valueOf(Severity.S2_HIGH));
        dto.setPriority("P1");
        dto.setCategory("BUG");
        dto.setEnvironment("PRODUCTION");
        dto.setDetectedAt(Date.valueOf("2026-06-26"));
        dto.setSlaDueAt(Date.valueOf("2026-06-28"));
        dto.setStatus(String.valueOf(IssueStatus.AWAITING_TENANT));
        when(issueRepository.nextIssueKeyNumber()).thenReturn(100L);
        Issue savedIssue = new Issue();
        savedIssue.setId(UUID.randomUUID());

        when(issueRepository.saveAndFlush(any(Issue.class))).thenReturn(savedIssue);
        service.create(dto);
        verify(issueRepository).nextIssueKeyNumber();
        verify(issueRepository).saveAndFlush(any(Issue.class));
    }

    @Test
    void testFindById() throws Exception {
        UUID uuid = UUID.randomUUID();
        Issue issue = new Issue();
        issue.setId(uuid);
        issue.setSummary("Issue Related to the Testing");
        when(issueRepository.findById(uuid)).thenReturn(Optional.of(issue));

        IssueDto result = service.findById(uuid);
        assertNotNull(result);

        verify(issueRepository).findById(uuid);
    }

    @Test
    void testChangeStatus() {
        UUID uuid = UUID.randomUUID();
        Issue issue = new Issue();
        issue.setId(uuid);
        issue.setStatus(IssueStatus.NEW);

        Map<String, Object> request = new HashMap<>();
        request.put("status", "CLOSED");
        request.put("notes", "Issue resolved");

        when(issueRepository.findById(uuid)).thenReturn(Optional.of(issue));

        when(issueRepository.save(any(Issue.class))).thenReturn(issue);

        service.changeStatus(uuid, request);
        assertEquals(IssueStatus.CLOSED, issue.getStatus());
        verify(issueRepository).findById(uuid);
        verify(issueRepository).save(issue);
        verify(historyRepository).save(any(IssueStatusHistory.class));

    }

    @Test
    void testDeleteByid() {
        UUID uuid = UUID.randomUUID();

        Issue issue = new Issue();
        issue.setStatus(IssueStatus.NEW);
        issue.setId(uuid);

        when(issueRepository.findById(uuid)).thenReturn(Optional.of(issue));
        doNothing().when(issueRepository).delete(issue);
        service.deleteById(uuid);
        verify(issueRepository).findById(uuid);
        verify(issueRepository).delete(issue);
    }

    @Test
    void testUpdate() {

        UUID uuid = UUID.randomUUID();

        Issue issue = new Issue();
        issue.setId(uuid);
        issue.setStatus(IssueStatus.NEW);

        IssueDto dto = new IssueDto();
        dto.setStatus(String.valueOf(IssueStatus.AWAITING_TENANT));
        dto.setTenantId(UUID.randomUUID());

        Issue entity = new Issue();
        entity.setId(uuid);
        entity.setStatus(IssueStatus.AWAITING_TENANT);
        when(issueRepository.findById(uuid)).thenReturn(Optional.of(issue));
        when(issueRepository.save(any(Issue.class))).thenReturn(entity);
        service.update(uuid, dto);
        verify(issueRepository).findById(uuid);
        verify(issueRepository).save(any(Issue.class));


    }

    @Test
    void testListOfIssues() {
        Pageable pageable = PageRequest.of(0, 10);
        Issue issue = new Issue();
        issue.setId(UUID.randomUUID());
        issue.setStatus(IssueStatus.NEW);
        Issue issues = new Issue();
        issues.setId(UUID.randomUUID());
        issues.setStatus(IssueStatus.AWAITING_TENANT);
        List<Issue> issueList = List.of(issue,issues);
        Page<Issue> issuePage = new PageImpl<>(issueList, pageable, 2);
        IssueDto dto = new IssueDto();
        dto.setId(issue.getId());
        when(issueRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(issuePage);
        try (MockedStatic<IssueConverter> mocked = Mockito.mockStatic(IssueConverter.class)) {
            mocked.when(() -> IssueConverter.toTransportModel(issue))
                    .thenReturn(dto);
            Page<IssueDto> result = service.list("tenant1", UUID.randomUUID(), IssueStatus.NEW,
                    Severity.S2_HIGH, UUID.randomUUID(), "search", pageable);
            assertNotNull(result);
            assertEquals(2, result.getTotalElements());

            verify(issueRepository).findAll(any(Specification.class), eq(pageable));
        }
    }
}


