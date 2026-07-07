package com.lnf.sentinel.controller;

import com.lnf.dto.sentinel.IssueWatcherDto;
import com.lnf.sentinel.service.IssueWatcherService;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = IssueWatcherControllerTest.TestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class IssueWatcherControllerTest {

    // 1. We create an isolated test configuration to cut ties with Application.java database setups
    @Configuration
    @Import(IssueWatcherController.class)
    @ImportAutoConfiguration(classes = WebMvcAutoConfiguration.class)
    static class TestConfig {
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IssueWatcherService service;

    @Test
    void testListWatchers() throws Exception {
        UUID issueId = UUID.randomUUID();

        IssueWatcherDto dto = new IssueWatcherDto();
        dto.setIssueId(issueId);

        Mockito.when(service.listWatchers(issueId))
                .thenReturn(List.of(dto));

        mockMvc.perform(
                        get("/lnf/sentinel/issues/watchers")
                                .param("issueId", issueId.toString())
                )
                .andExpect(status().isOk());

        Mockito.verify(service).listWatchers(issueId);
    }

    @Test
    void testAddWatcher() throws Exception {
        UUID issueId = UUID.randomUUID();
        String userEmail = "lnf@gmail.com";
        String userName = "siva";

        doNothing().when(service)
                .addWatcher(issueId, userEmail, userName);

        mockMvc.perform(
                        post("/lnf/sentinel/issues/watchers")
                                .param("issueId", issueId.toString())
                                .param("userEmail", userEmail)
                                .param("userName", userName)
                )
                .andExpect(status().isCreated());

        Mockito.verify(service)
                .addWatcher(issueId, userEmail, userName);
    }

    @Test
    void testRemoveWatcher() throws Exception {
        UUID issueId = UUID.randomUUID();
        String userEmail = "lnf@gmail.com";

        doNothing().when(service)
                .removeWatcher(issueId, userEmail);

        mockMvc.perform(
                        delete("/lnf/sentinel/issues/watchers")
                                .param("issueId", issueId.toString())
                                .param("userEmail", userEmail)
                )
                .andExpect(status().isNoContent());

        Mockito.verify(service)
                .removeWatcher(issueId, userEmail);
    }

}
