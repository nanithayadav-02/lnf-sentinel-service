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

    @GetMapping("/comments/{id}")
    public List<IssueCommentDto> listComments(@PathVariable UUID id) {
        return service.listComments(id);
    }

    @PostMapping("/comments/{id}")
    public void addComment(@PathVariable UUID id,
                           @Valid @RequestBody IssueCommentDto req) {
        service.addComment(id, req);
    }

}
