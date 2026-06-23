package com.lnf.sentinel.service;

import com.lnf.dto.sentinel.IssueWatcherDto;
import com.lnf.exception.LnFEntityNotFoundException;
import com.lnf.sentinel.converter.IssueWatcherConverter;
import com.lnf.sentinel.model.IssueWatcher;
import com.lnf.sentinel.repository.IssueRepository;
import com.lnf.sentinel.repository.IssueWatcherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class IssueWatcherService {

    private final IssueWatcherRepository repository;
    private final IssueRepository issueRepository;

    @Transactional
    public void addWatcher(UUID issueId, UUID userId) {
        searchForIssueId(issueId);
        IssueWatcher w = repository.findByIssueIdAndUserId(issueId, userId)
                .orElseGet(() -> {
                    IssueWatcher watcher=new IssueWatcher();
                    watcher.setIssueId(issueId);
                    watcher.setUserId(userId);
                    return repository.save(watcher);
                });
        IssueWatcherConverter.toTransportModel(w);

    }

    @Transactional
    public void removeWatcher(UUID issueId, UUID userId) {
        searchForIssueId(issueId);
        repository.findByIssueIdAndUserId(issueId, userId)
                .ifPresent(repository::delete);
    }

    @Transactional(readOnly = true)
    public List<IssueWatcherDto> listWatchers(UUID issueId) {
        searchForIssueId(issueId);
        return repository.findByIssueId(issueId)
                .stream().map(IssueWatcherConverter::toTransportModel).toList();
    }

    private void searchForIssueId(UUID id) {
        issueRepository.findById(id)
                .orElseThrow(() -> new LnFEntityNotFoundException("Issue not found: " + id));
    }

}
