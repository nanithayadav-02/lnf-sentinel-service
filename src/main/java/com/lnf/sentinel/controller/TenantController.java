package com.lnf.sentinel.controller;

import com.lnf.sentinel.dto.CreateTenantRequest;
import com.lnf.sentinel.dto.TenantResponse;
import com.lnf.sentinel.service.TenantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
@Tag(name = "Tenants", description = "Tenant administration")
public class TenantController {

    private final TenantService tenantService;

    @GetMapping
    public List<TenantResponse> list() {
        return tenantService.list();
    }

    @PostMapping
    public ResponseEntity<TenantResponse> create(@Valid @RequestBody CreateTenantRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantService.create(req));
    }

    @GetMapping("/{id}")
    public TenantResponse get(@PathVariable Long id) {
        return tenantService.get(id);
    }
}
