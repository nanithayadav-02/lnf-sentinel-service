package com.lnf.sentinel.controller;

import com.lnf.sentinel.domain.enums.IssueStatus;
import com.lnf.sentinel.domain.enums.Severity;
import com.lnf.sentinel.dto.*;
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

@RestController
@RequestMapping("/api/v1/issues")
@RequiredArgsConstructor
@Tag(name = "Issues", description = "Production issues — the engineering record of truth")
public class IssueController {

    private final IssueService issueService;
    private final CollaborationService collaborationService;

    @GetMapping
    @Operation(summary = "List issues (filterable, paginated)")
    public Page<IssueResponse> list(
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
    public ResponseEntity<IssueResponse> create(@Valid @RequestBody CreateIssueRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(issueService.create(req));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one issue (tenant-scoped)")
    public IssueResponse get(@PathVariable Long id) {
        return issueService.get(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update mutable fields")
    public IssueResponse update(@PathVariable Long id, @Valid @RequestBody UpdateIssueRequest req) {
        return issueService.update(id, req);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Change status (+ resolution/root cause on resolve/close)")
    public IssueResponse changeStatus(@PathVariable Long id, @Valid @RequestBody ChangeStatusRequest req) {
        return issueService.changeStatus(id, req);
    }

    // ---- comments ----
    @GetMapping("/{id}/comments")
    public List<CommentResponse> listComments(@PathVariable Long id) {
        return collaborationService.listComments(id);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long id,
                                                      @Valid @RequestBody CreateCommentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(collaborationService.addComment(id, req));
    }

    // ---- links ----
    @GetMapping("/{id}/links")
    public List<LinkResponse> listLinks(@PathVariable Long id) {
        return collaborationService.listLinks(id);
    }

    @PostMapping("/{id}/links")
    public ResponseEntity<LinkResponse> createLink(@PathVariable Long id,
                                                   @Valid @RequestBody CreateLinkRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(collaborationService.createLink(id, req));
    }

    @DeleteMapping("/links/{linkId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLink(@PathVariable Long linkId) {
        collaborationService.deleteLink(linkId);
    }

    // ---- watchers ----
    @GetMapping("/{id}/watchers")
    public List<WatcherResponse> listWatchers(@PathVariable Long id) {
        return collaborationService.listWatchers(id);
    }

    @PostMapping("/{id}/watchers/{userId}")
    @Operation(summary = "Watch an issue (idempotent)")
    public WatcherResponse addWatcher(@PathVariable Long id, @PathVariable Long userId) {
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
    public List<AttachmentResponse> listAttachments(@PathVariable Long id) {
        return collaborationService.listAttachments(id);
    }

    @PostMapping("/{id}/attachments")
    @Operation(summary = "Upload attachment metadata (binary lives in blob storage)")
    public ResponseEntity<AttachmentResponse> addAttachment(@PathVariable Long id,
                                                            @Valid @RequestBody CreateAttachmentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(collaborationService.addAttachment(id, req));
    }
}
