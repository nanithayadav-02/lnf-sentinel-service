package com.lnf.sentinel.controller;

import com.lnf.dto.sentinel.IssueAuditHistoryDto;
import com.lnf.sentinel.service.IssueAuditHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/lnf/sentinel/issues/audit-history")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Issue Audit History", description = "Issue Audit History APIs")
public class IssueAuditHistoryController {

    private final IssueAuditHistoryService issueAuditHistoryService;

    @GetMapping
    @Operation(summary = "Get all issue audit history records")
    public List<IssueAuditHistoryDto> findAll() {
        return issueAuditHistoryService.findAll();
    }

    @PostMapping("/{issueId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create issue audit history")
    public void create(@RequestBody IssueAuditHistoryDto resource, @PathVariable  UUID issueId) {
        issueAuditHistoryService.create(resource,issueId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get issue audit history by Id")
    public IssueAuditHistoryDto findById(@PathVariable UUID id) {
        return issueAuditHistoryService.findById(id);
    }
}