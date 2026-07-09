package com.lnf.sentinel.controller;

import com.lnf.dto.sentinel.FileDto;
import com.lnf.sentinel.service.IssueFilesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/lnf/sentinel/issues")
@RequiredArgsConstructor
public class IssueFilesController {

    private final IssueFilesService service;

    @GetMapping(value = "/{issueId}/files")
    @ResponseStatus(HttpStatus.OK)
    public List<FileDto> findFilesWithModuleName(@PathVariable UUID issueId) {
        return service.findByIssueId(issueId);
    }

    @GetMapping(value = "/{issueId}/files/{name}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> findFileByIssueIdAndName(@PathVariable UUID issueId, @PathVariable String name) {
        return service.findById(issueId, name);
    }

}
