package com.lnf.sentinel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lnf.dto.sentinel.IssueLinkDto;
import com.lnf.sentinel.service.IssueLinkService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@ContextConfiguration(classes = IssueLinkControllerTest.TestConfig.class)
@AutoConfigureMockMvc(addFilters = false) // Disables spring security filters if present
class IssueLinkControllerTest {

    // Isolate this test from the main Application configuration
    @Configuration
    @Import(issueLinkController.class)
    @ImportAutoConfiguration(classes = {WebMvcAutoConfiguration.class})
    static class TestConfig {
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IssueLinkService service;

   @Test
    void shouldListLinks() throws Exception {
       UUID issueId = UUID.randomUUID();

       Map<String, Object> link = new HashMap<>();
       link.put("sourceIssueId", issueId.toString());

       Mockito.when(service.findByIssueId(issueId))
               .thenReturn(List.of(link));

       mockMvc.perform(get("/lnf/sentinel/issues/links/{issueId}", issueId))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].sourceIssueId")
                       .value(issueId.toString()));
    }

    @Test
    void shouldCreateLink() throws Exception {
        UUID issueId = UUID.randomUUID();

        IssueLinkDto dto = new IssueLinkDto();
        dto.setSourceIssueId(issueId);
        dto.setTargetIssueId(UUID.randomUUID());

        mockMvc.perform(post("/lnf/sentinel/issues/links/{id}", issueId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        Mockito.verify(service)
                .createLink(eq(issueId), any(IssueLinkDto.class));
    }

    @Test
    void shouldDeleteLink() throws Exception {
        UUID linkId = UUID.randomUUID();

        mockMvc.perform(delete("/lnf/sentinel/issues/links/{linkId}", linkId))
                .andExpect(status().isNoContent());

        Mockito.verify(service).deleteById(linkId);
    }
}