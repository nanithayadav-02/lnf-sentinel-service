package com.lnf.sentinel.controller;

import com.lnf.sentinel.service.IssueAuditHistoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lnf/sentinel/issues/audit-history")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Issue Audit History", description = "Issue Audit History APIs")
public class IssueAuditHistoryController {

    private final IssueAuditHistoryService issueAuditHistoryService;

    @GetMapping("/issueAuditHistory")
    public ResponseEntity<?> getIssueAuditHistory(){
        return ResponseEntity.ok(issueAuditHistoryService.getIssueAuditHistory());
    }
}