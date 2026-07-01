package com.lnf.sentinel.controller;

import com.lnf.dto.sentinel.IssueDto;
import com.lnf.sentinel.model.enums.IssueStatus;
import com.lnf.sentinel.model.enums.Severity;
import com.lnf.sentinel.service.IssueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public Page<IssueDto> list(@RequestParam(required = false) String tenantName,
                               @RequestParam(required = false) UUID tenantId,
                               @RequestParam(required = false) IssueStatus status,
                               @RequestParam(required = false) Severity severity,
                               @RequestParam(required = false) UUID assigneeId,
                               @RequestParam(required = false) String search,
                               Pageable pageable) {
        return issueService.list(tenantName, tenantId, status, severity, assigneeId, search, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an issue (generates key, sets SLA, writes initial history)")
    public void create(@RequestBody IssueDto resource) {
        issueService.create(resource);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one issue (tenant-scoped)")
    public IssueDto findById(@PathVariable UUID id) {
        return issueService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update mutable fields")
    public void update(@PathVariable UUID id, @RequestBody final IssueDto resource) {
        issueService.update(id, resource);
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

}
