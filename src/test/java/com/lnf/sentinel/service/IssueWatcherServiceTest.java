package com.lnf.sentinel.service;

import com.lnf.dto.sentinel.IssueWatcherDto;
import com.lnf.exception.LnFEntityNotFoundException;
import com.lnf.sentinel.model.Issue;
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
            String userEmail = "LNf@gmail.com";
            String userName = "lnf";

            when(issueRepository.findById(issueId)).thenReturn(Optional.of(new Issue()));
            when(repository.findByIssueIdAndUserEmail(issueId, userEmail)).thenReturn(Optional.empty());

            // Mocking save behavior since orElseGet falls back to repository.save()
            when(repository.save(any(IssueWatcher.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act & Assert
            assertDoesNotThrow(() -> service.addWatcher(issueId, userEmail, userName));
            verify(repository, times(1)).save(any(IssueWatcher.class));
        }

        @Test
        void shouldNotDuplicateWatcherWhenAlreadyWatching() {
            // Arrange
            UUID issueId = UUID.randomUUID();
            String userEmail = "LNf";
            String userName = "LeverAndFulcrum";

            when(issueRepository.findById(issueId)).thenReturn(Optional.of(new Issue()));

            // Act
            service.addWatcher(issueId, userEmail, userName);

            // Assert - verify save was never executed because it already exists
            verify(repository, never()).save(any(IssueWatcher.class));
        }

        @Test
        void shouldThrowExceptionWhenIssueDoesNotExist() {

            UUID issueId = UUID.randomUUID();
            String userEmail = "LNf";
            String userName = "LeverAndFulcrum";

            when(issueRepository.findById(issueId)).thenReturn(Optional.empty());


            LnFEntityNotFoundException exception = assertThrows(LnFEntityNotFoundException.class, () ->
                    service.addWatcher(issueId, userEmail, userName)
            );
            assertTrue(exception.getMessage().contains("Issue not found:"));
            verify(repository, never()).findByIssueIdAndUserEmail(any(), any());
        }
    }

    @Nested
    class RemoveWatcherTests {

        @Test
        void shouldRemoveWatcherIfExists() {

            UUID issueId = UUID.randomUUID();
            String userEmail = "sample@test.com";
            IssueWatcher existingWatcher = new IssueWatcher();

            when(issueRepository.findById(issueId)).thenReturn(Optional.of(new Issue()));
            when(repository.findByIssueIdAndUserEmail(issueId, userEmail)).thenReturn(Optional.of(existingWatcher));


            service.removeWatcher(issueId, userEmail);


            verify(repository, times(1)).delete(existingWatcher);
        }

        @Test
        void shouldDoNothingWhenWatcherDoesNotExist() {

            UUID issueId = UUID.randomUUID();
            String userEmail = "sample@test.com";

            when(issueRepository.findById(issueId)).thenReturn(Optional.of(new Issue()));
            when(repository.findByIssueIdAndUserEmail(issueId, userEmail)).thenReturn(Optional.empty());


            service.removeWatcher(issueId, userEmail);


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
            watcher.setUserEmail("sample@test.com");

            when(issueRepository.findById(issueId)).thenReturn(Optional.of(new Issue()));
            when(repository.findByIssueId(issueId)).thenReturn(List.of(watcher));


            List<IssueWatcherDto> result = service.listWatchers(issueId);


            assertNotNull(result);
            assertEquals(1, result.size());
            verify(repository, times(1)).findByIssueId(issueId);
        }
    }
}