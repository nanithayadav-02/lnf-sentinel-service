package com.lnf.sentinel.controller;

import com.lnf.dto.sentinel.IssueWatcherDto;
import com.lnf.sentinel.service.IssueWatcherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("lnf/sentinel/issues")
@RequiredArgsConstructor
public class IssueWatcherController {

    private final IssueWatcherService service;

    @GetMapping("/watchers")
    @ResponseStatus(HttpStatus.OK)
    public List<IssueWatcherDto> listWatchers(@RequestParam UUID issueId) {
        return service.listWatchers(issueId);
    }

    @PostMapping("/watchers")
    @ResponseStatus(HttpStatus.CREATED)
    public void addWatcher(@RequestParam UUID issueId, @RequestParam UUID userId,@RequestParam String createdBy) {
        service.addWatcher(issueId, userId,createdBy);
    }

    @DeleteMapping("/watchers")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeWatcher(@RequestParam UUID issueId, @RequestParam UUID userId) {
        service.removeWatcher(issueId, userId);
    }

}
