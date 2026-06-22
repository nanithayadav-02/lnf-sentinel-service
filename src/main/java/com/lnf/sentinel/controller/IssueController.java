package com.lnf.sentinel.controller;

import com.lnf.dto.sentinel.*;
import com.lnf.sentinel.model.enums.IssueStatus;
import com.lnf.sentinel.model.enums.Severity;
import com.lnf.sentinel.service.CollaborationService;
import com.lnf.sentinel.service.IssueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/issues")
@RequiredArgsConstructor
@Tag(name = "Issues", description = "Production issues — the engineering record of truth")
public class IssueController {

    private final IssueService issueService;
    private final CollaborationService collaborationService;

    @GetMapping
    @Operation(summary = "List issues (filterable, paginated)")
    public Page<IssueDto> list(
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false) IssueStatus status,
            @RequestParam(required = false) Severity severity,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return issueService.list(tenantId, status, severity, assigneeId, search, pageable);
    }

    @PostMapping
    @Operation(summary = "Create an issue (generates key, sets SLA, writes initial history)")
    public ResponseEntity<IssueDto> create(@Valid @RequestBody IssueDto req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(issueService.create(req));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one issue (tenant-scoped)")
    public IssueDto get(@PathVariable Long id) {
        return issueService.get(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update mutable fields")
    public IssueDto update(@PathVariable Long id, @Valid @RequestBody IssueDto req) {
        return issueService.update(id, req);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Change status (+ resolution/root cause on resolve/close)")
    public IssueDto changeStatus(@PathVariable Long id, @Valid @RequestBody IssueDto req) {
        return issueService.changeStatus(id, req);
    }

    // ---- comments ----
    @GetMapping("/{id}/comments")
    public List<IssueCommentDto> listComments(@PathVariable UUID id) {
        return collaborationService.listComments(id);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<IssueCommentDto> addComment(@PathVariable UUID id,
                                                      @Valid @RequestBody IssueCommentDto req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(collaborationService.addComment(id, req));
    }

    // ---- links ----
    @GetMapping("/{id}/links")
    public List<IssueLinkDto> listLinks(@PathVariable UUID id) {
        return collaborationService.listLinks(id);
    }

    @PostMapping("/{id}/links")
    public ResponseEntity<IssueLinkDto> createLink(@PathVariable Long id,
                                                   @Valid @RequestBody IssueLinkDto req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(collaborationService.createLink(id, req));
    }

    @DeleteMapping("/links/{linkId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLink(@PathVariable UUID linkId) {
        collaborationService.deleteLink(linkId);
    }

    // ---- watchers ----
    @GetMapping("/{id}/watchers")
    public List<IssueWatcherDto> listWatchers(@PathVariable UUID id) {
        return collaborationService.listWatchers(id);
    }

    @PostMapping("/{id}/watchers/{userId}")
    @Operation(summary = "Watch an issue (idempotent)")
    public IssueWatcherDto addWatcher(@PathVariable Long id, @PathVariable Long userId) {
        return collaborationService.addWatcher(id, userId);
    }

    @DeleteMapping("/{id}/watchers/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Unwatch an issue (idempotent)")
    public void removeWatcher(@PathVariable Long id, @PathVariable Long userId) {
        collaborationService.removeWatcher(id, userId);
    }

    // ---- attachments ----
    @GetMapping("/{id}/attachments")
    public List<IssueAttachmentDto> listAttachments(@PathVariable Long id) {
        return collaborationService.listAttachments(id);
    }

    @PostMapping("/{id}/attachments")
    @Operation(summary = "Upload attachment metadata (binary lives in blob storage)")
    public ResponseEntity<IssueAttachmentDto> addAttachment(@PathVariable Long id,
                                                            @Valid @RequestBody IssueAttachmentDto req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(collaborationService.addAttachment(id, req));
    }
}
