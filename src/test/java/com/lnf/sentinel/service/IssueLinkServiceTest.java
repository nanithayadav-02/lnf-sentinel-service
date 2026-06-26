package com.lnf.sentinel.service;

import com.lnf.dto.sentinel.IssueLinkDto;
import com.lnf.exception.LnFBadRequestException;
import com.lnf.exception.LnFEntityNotFoundException;
import com.lnf.sentinel.model.IssueLink;
import com.lnf.sentinel.model.Issue;
import com.lnf.sentinel.model.enums.LinkType;
import com.lnf.sentinel.repository.IssueLinkRepository;
import com.lnf.sentinel.repository.IssueRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssueLinkServiceTest {

    @Mock
    private IssueLinkRepository repository;

    @Mock
    private IssueRepository issueRepository;

    @InjectMocks
    private IssueLinkService service;

    @Nested
    class CreateLinkTests {

        @Test
        void shouldCreateLinkSuccessfully() {

            UUID sourceId = UUID.randomUUID();
            UUID targetId = UUID.randomUUID();

            IssueLinkDto dto = new IssueLinkDto();
            dto.setTargetIssueId(targetId);
            dto.setLinkType("RELATES_TO"); // Assuming RELATES_TO is a valid LinkType enum

            when(issueRepository.findById(sourceId)).thenReturn(Optional.of(new Issue()));
            when(issueRepository.findById(targetId)).thenReturn(Optional.of(new Issue()));


            when(repository.existsBySourceIssueIdAndTargetIssueIdAndLinkType(sourceId, targetId, LinkType.RELATES_TO))
                    .thenReturn(false);


            assertDoesNotThrow(() -> service.createLink(sourceId, dto));
            verify(repository, times(1)).save(any(IssueLink.class));
        }

        @Test
        void shouldThrowExceptionWhenLinkingIssueToItself() {

            UUID issueId = UUID.randomUUID();
            IssueLinkDto dto = new IssueLinkDto();
            dto.setTargetIssueId(issueId); // Same ID

            when(issueRepository.findById(issueId)).thenReturn(Optional.of(new Issue()));


            LnFBadRequestException exception = assertThrows(LnFBadRequestException.class, () ->
                    service.createLink(issueId, dto)
            );
            assertEquals("An issue cannot be linked to itself", exception.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        void shouldThrowExceptionWhenLinkAlreadyExists() {

            UUID sourceId = UUID.randomUUID();
            UUID targetId = UUID.randomUUID();

            IssueLinkDto dto = new IssueLinkDto();
            dto.setTargetIssueId(targetId);
            dto.setLinkType("RELATES_TO");

            when(issueRepository.findById(sourceId)).thenReturn(Optional.of(new Issue()));
            when(issueRepository.findById(targetId)).thenReturn(Optional.of(new Issue()));
            when(repository.existsBySourceIssueIdAndTargetIssueIdAndLinkType(sourceId, targetId, LinkType.RELATES_TO))
                    .thenReturn(true);


            LnFBadRequestException exception = assertThrows(LnFBadRequestException.class, () ->
                    service.createLink(sourceId, dto)
            );
            assertEquals("That link already exists", exception.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        void shouldThrowExceptionWhenSourceIssueNotFound() {

            UUID sourceId = UUID.randomUUID();
            IssueLinkDto dto = new IssueLinkDto();

            when(issueRepository.findById(sourceId)).thenReturn(Optional.empty());

            // Act & Assert
            LnFEntityNotFoundException exception = assertThrows(LnFEntityNotFoundException.class, () ->
                    service.createLink(sourceId, dto)
            );
            assertTrue(exception.getMessage().contains("Issue not found:"));
        }
    }

    @Nested
    class FindByIssueIdTests {

        @Test
        void shouldReturnLinksWhenIssueExists() {

            UUID issueId = UUID.randomUUID();
            IssueLink mockLink = new IssueLink();
            mockLink.setSourceIssueId(issueId);
            mockLink.setTargetIssueId(UUID.randomUUID());
            mockLink.setLinkType(LinkType.RELATES_TO);

            when(issueRepository.findById(issueId)).thenReturn(Optional.of(new Issue()));
            when(repository.findBySourceIssueId(issueId)).thenReturn(List.of(mockLink));


            List<IssueLinkDto> result = service.findByIssueId(issueId);


            assertNotNull(result);
            assertEquals(1, result.size());
            verify(repository, times(1)).findBySourceIssueId(issueId);
        }
    }

    @Nested
    class DeleteByIdTests {

        @Test
        void shouldDeleteLinkSuccessfully() {
            // Arrange
            UUID linkId = UUID.randomUUID();
            IssueLink mockLink = new IssueLink();

            when(repository.findById(linkId)).thenReturn(Optional.of(mockLink));


            service.deleteById(linkId);


            verify(repository, times(1)).delete(mockLink);
        }

        @Test
        void shouldThrowExceptionWhenLinkToDeleteNotFound() {
            // Arrange
            UUID linkId = UUID.randomUUID();
            when(repository.findById(linkId)).thenReturn(Optional.empty());


            LnFEntityNotFoundException exception = assertThrows(LnFEntityNotFoundException.class, () ->
                    service.deleteById(linkId)
            );
            assertTrue(exception.getMessage().contains("Link not found:"));
            verify(repository, never()).delete(any());
        }
    }
}