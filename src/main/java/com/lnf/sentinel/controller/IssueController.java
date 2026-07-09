package com.lnf.sentinel.controller;

import com.lnf.dto.common.PageRequestDto;
import com.lnf.dto.sentinel.IssueDto;
import com.lnf.sentinel.model.enums.IssueStatus;
import com.lnf.sentinel.model.enums.Severity;
import com.lnf.sentinel.service.IssueService;
import com.lnf.service.common.page.PageableAsQueryParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/lnf/sentinel/issues")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Issues", description = "Production issues — the engineering record of truth")
public class IssueController {

    private final IssueService issueService;

    @GetMapping
    @Operation(summary = "List issues (filterable, paginated)")
    public ResponseEntity<?> list(@RequestParam(required = false) String tenantName,
                                  @RequestParam(required = false) UUID tenantId,
                                  @RequestParam(required = false) IssueStatus status,
                                  @RequestParam(required = false) Severity severity,
                                  @RequestParam(required = false) UUID assigneeId,
                                  @RequestParam(required = false) String search,
                                  @PageableAsQueryParam PageRequestDto pageRequest) {
        if (pageRequest != null && pageRequest.getPage() != null) {
            Page<IssueDto> issues = issueService.list(tenantName, tenantId, status, severity, assigneeId, search, pageRequest);
            return ResponseEntity.ok(issues);
        }
        return ResponseEntity.ok(issueService.searchForIssue(search));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one issue (tenant-scoped)")
    public IssueDto findById(@PathVariable UUID id) {
        return issueService.findById(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an issue (generates key, sets SLA, writes initial history)")
    public void create(@RequestPart IssueDto resource, @RequestPart(required = false) MultipartFile[] files) {
        issueService.create(resource, files);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update mutable fields")
    public void update(@PathVariable UUID id, @RequestBody final IssueDto resource,
                       @RequestPart(required = false) MultipartFile[] files) {
        issueService.update(id, resource, files);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Change status (+ resolution/root cause on resolve/close)")
    public ResponseEntity<Void> changeStatus(@PathVariable UUID id, @RequestBody final Map<String, Object> request) {
        issueService.changeStatus(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Issue By Id")
    public void delete(@PathVariable UUID id) {
        issueService.deleteById(id);
    }

    @GetMapping("issuesBySeverity")
    public Map<String, Object> getTotalIssuesBySeverity(@RequestParam(required = false) String tenantName) {
        return issueService.getIssueCountBySeverity(tenantName);
    }

}
