package com.lnf.sentinel.controller;



import com.lnf.dto.sentinel.MetricSummaryDto;
import com.lnf.sentinel.service.MetricsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/metrics")
@RequiredArgsConstructor
@Tag(name = "Metrics", description = "Dashboard headline n" + "umbers")
public class MetricsController {

    private final MetricsService metricsService;

    @GetMapping("/summary")
    public MetricSummaryDto summary() {
        return metricsService.summary();
    }
}

