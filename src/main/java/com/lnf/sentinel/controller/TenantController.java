package com.lnf.sentinel.controller;


//import com.lnf.sentinel.service.TenantService;
import com.lnf.dto.sentinel.TenantDto;
import com.lnf.sentinel.service.TenantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
@Tag(name = "Tenants", description = "Tenant administration")
public class TenantController {

    private final TenantService tenantService;

    @GetMapping
    public List<TenantDto> list() {
        return tenantService.list();
    }

    @PostMapping
    public ResponseEntity<TenantDto> create(@Valid @RequestBody TenantDto req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantService.create(req));
    }

    @GetMapping("/{id}")
    public TenantDto get(@PathVariable UUID id) {
        return tenantService.get(id);
    }
}
