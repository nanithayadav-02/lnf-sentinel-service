package com.lnf.sentinel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lnf.dto.sentinel.MetricSummaryDto;
import com.lnf.sentinel.service.MetricsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MetricsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MetricsService metricsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnMetricsSummary() throws Exception {

        MetricSummaryDto dto = new MetricSummaryDto();


        when(metricsService.summary(UUID.randomUUID())).thenReturn(dto);

        mockMvc.perform(get("/api/v1/metrics/summary"))
                .andExpect(status().isOk());
    }
}