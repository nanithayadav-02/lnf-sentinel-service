package com.lnf.sentinel.controller;

import com.lnf.dto.sentinel.IssueLinkDto;
import com.lnf.sentinel.service.IssueLinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sentinel/issues")
@RequiredArgsConstructor
public class issueLinkController {

    private final IssueLinkService service;

    @GetMapping("/links/{id}")
    public List<IssueLinkDto> listLinks(@PathVariable UUID id) {
        return service.findByIssueId(id);
    }

    @PostMapping("/links/{id}")
    public void createLink(@PathVariable UUID id,
                           @Valid @RequestBody IssueLinkDto resource) {
        service.createLink(id, resource);
    }

    @DeleteMapping("/links/{linkId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLink(@PathVariable UUID linkId) {
        service.deleteById(linkId);
    }

}
