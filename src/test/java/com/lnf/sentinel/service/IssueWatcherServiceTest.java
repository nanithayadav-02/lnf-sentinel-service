package com.lnf.sentinel.service;

import com.lnf.dto.sentinel.IssueWatcherDto;
import com.lnf.exception.LnFEntityNotFoundException;
import com.lnf.sentinel.model.Issue; // Assuming this entity exists
import com.lnf.sentinel.model.IssueWatcher;
import com.lnf.sentinel.repository.IssueRepository;
import com.lnf.sentinel.repository.IssueWatcherRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssueWatcherServiceTest {

    @Mock
    private IssueWatcherRepository repository;

    @Mock
    private IssueRepository issueRepository;

    @InjectMocks
    private IssueWatcherService service;

    @Nested
    class AddWatcherTests {

             @Test
             void shouldAddWatcherWhenNotAlreadyWatching() {
                 // Arrange
                 UUID issueId = UUID.randomUUID();
                 UUID userId = UUID.randomUUID();
                 String createdBy="LNf";

                 when(issueRepository.findById(issueId)).thenReturn(Optional.of(new Issue()));
                 when(repository.findByIssueIdAndUserIdAndCreatedBy(issueId, userId,createdBy)).thenReturn(Optional.empty());

                 // Mocking save behavior since orElseGet falls back to repository.save()
                 when(repository.save(any(IssueWatcher.class))).thenAnswer(invocation -> invocation.getArgument(0));

                 // Act & Assert
                 assertDoesNotThrow(() -> service.addWatcher(issueId, userId,createdBy));
                 verify(repository, times(1)).save(any(IssueWatcher.class));
             }

             @Test
             void shouldNotDuplicateWatcherWhenAlreadyWatching() {
                 // Arrange
                 UUID issueId = UUID.randomUUID();
                 UUID userId = UUID.randomUUID();
                 String createdBy="LNF";
                 IssueWatcher existingWatcher = new IssueWatcher();

                 when(issueRepository.findById(issueId)).thenReturn(Optional.of(new Issue()));
                 when(repository.findByIssueIdAndUserIdAndCreatedBy(issueId, userId,createdBy)).thenReturn(Optional.of(existingWatcher));

                 // Act
                 service.addWatcher(issueId, userId,createdBy);

                 // Assert - verify save was never executed because it already exists
                 verify(repository, never()).save(any(IssueWatcher.class));
             }
        @Test
        void shouldThrowExceptionWhenIssueDoesNotExist() {

            UUID issueId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            String createdBy="LNf";

            when(issueRepository.findById(issueId)).thenReturn(Optional.empty());


            LnFEntityNotFoundException exception = assertThrows(LnFEntityNotFoundException.class, () ->
                    service.addWatcher(issueId, userId,createdBy)
            );
            assertTrue(exception.getMessage().contains("Issue not found:"));
            verify(repository, never()).findByIssueIdAndUserId(any(), any());
        }
    }

    @Nested
    class RemoveWatcherTests {

        @Test
        void shouldRemoveWatcherIfExists() {

            UUID issueId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            IssueWatcher existingWatcher = new IssueWatcher();

            when(issueRepository.findById(issueId)).thenReturn(Optional.of(new Issue()));
            when(repository.findByIssueIdAndUserId(issueId, userId)).thenReturn(Optional.of(existingWatcher));


            service.removeWatcher(issueId, userId);


            verify(repository, times(1)).delete(existingWatcher);
        }

        @Test
        void shouldDoNothingWhenWatcherDoesNotExist() {

            UUID issueId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(issueRepository.findById(issueId)).thenReturn(Optional.of(new Issue()));
            when(repository.findByIssueIdAndUserId(issueId, userId)).thenReturn(Optional.empty());


            service.removeWatcher(issueId, userId);


            verify(repository, never()).delete(any(IssueWatcher.class));
        }
    }

    @Nested
    class ListWatchersTests {

        @Test
        void shouldReturnWatchersList() {

            UUID issueId = UUID.randomUUID();
            IssueWatcher watcher = new IssueWatcher();
            watcher.setIssueId(issueId);
            watcher.setUserId(UUID.randomUUID());

            when(issueRepository.findById(issueId)).thenReturn(Optional.of(new Issue()));
            when(repository.findByIssueId(issueId)).thenReturn(List.of(watcher));


            List<IssueWatcherDto> result = service.listWatchers(issueId);


            assertNotNull(result);
            assertEquals(1, result.size());
            verify(repository, times(1)).findByIssueId(issueId);
        }
    }
}