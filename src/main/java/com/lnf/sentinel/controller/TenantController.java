package com.lnf.sentinel.controller;


import com.lnf.dto.sentinel.TenantDto;
import com.lnf.sentinel.service.TenantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/lnf/sentinel/tenant")
@RequiredArgsConstructor
@Tag(name = "Tenants", description = "Tenant administration")
public class TenantController {

    private final TenantService tenantService;

    @GetMapping
    public List<TenantDto> list() {
        return tenantService.list();
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody TenantDto resource) {
        tenantService.create(resource);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{tenantId}")
    public TenantDto get(@PathVariable UUID tenantId) {
        return tenantService.get(tenantId);
    }


    @GetMapping("/issuesForEachTenant")
    public Map<String,Object> getIssuesForEachTenant(){
        return tenantService.getIssuesforEachTenant();
    }

}
