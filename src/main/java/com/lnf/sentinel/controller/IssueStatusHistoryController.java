package com.lnf.sentinel.controller;

import com.lnf.dto.sentinel.IssueStatusHistoryDto;
import com.lnf.sentinel.service.IssueStatusHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf/sentinel/issues/status-history")
public class IssueStatusHistoryController {

    private final IssueStatusHistoryService issueStatusHistoryService;

    @PostMapping
    public ResponseEntity<IssueStatusHistoryDto> create(@RequestBody IssueStatusHistoryDto dto) {
        IssueStatusHistoryDto response = issueStatusHistoryService.create(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<IssueStatusHistoryDto>> getAll() {
        List<IssueStatusHistoryDto> response = issueStatusHistoryService.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{issueId}")
    public ResponseEntity<List<IssueStatusHistoryDto>> getByIssueId(@PathVariable UUID issueId) {
        List<IssueStatusHistoryDto> response = issueStatusHistoryService.getByIssueId(issueId);
        return ResponseEntity.ok(response);
    }

}