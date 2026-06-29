package com.lnf.sentinel.controller;

import com.lnf.dto.sentinel.IssueCommentDto;
import com.lnf.sentinel.service.IssueCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sentinel/issues")
@RequiredArgsConstructor
public class IssueCommentController {

    private final IssueCommentService service;

    @GetMapping("/comments/{issueId}")
    public List<IssueCommentDto> listComments(@PathVariable UUID issueId) {
        return service.listComments(issueId);
    }

    @PostMapping("/comments/{issueId}")
    public void addComment(@PathVariable UUID issueId,
                           @Valid @RequestBody IssueCommentDto req) {
        service.addComment(issueId, req);
    }

}
