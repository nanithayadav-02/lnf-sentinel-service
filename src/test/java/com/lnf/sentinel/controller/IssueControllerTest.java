package com.lnf.sentinel.controller;

import static org.aspectj.bridge.MessageUtil.fail;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lnf.dto.common.PageRequestDto;
import com.lnf.dto.sentinel.IssueDto;
import com.lnf.sentinel.BaseTestClass;
import com.lnf.sentinel.model.enums.IssueStatus;
import com.lnf.sentinel.model.enums.Severity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class IssueControllerTest extends BaseTestClass {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OAuth2AuthorizedClientManager authorizedClientManager;

    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @BeforeAll
    void beforeAll() {
        //To Be Implemented
    }

    @BeforeEach
    void setUp() {
        // To be implemented
    }



    @Test
    void testFindById() throws Exception {

        UUID issueId = UUID.fromString("aea3d132-dbf7-4d5f-a2b1-dd570bd32b43");
        IssueDto issueDto = mockIssueType();

        given(issueService.findById(any(UUID.class))).willReturn(issueDto);

        mockMvc.perform(get("/lnf/sentinel/issues/{id}",issueId)).andExpect(status().isOk());
        verify(issueService).findById(any(UUID.class));
    }

    @Test
    void testCreate() {
        IssueDto issueDto=mockIssueType();

        String url = "/lnf/sentinel/issues";

        doNothing().when(issueService).create(any(IssueDto.class));

        try {
            mockMvc.perform(MockMvcRequestBuilders.post(url)
                            .contentType(APPLICATION_JSON)
                            .content(asJsonString(issueDto)))
                    .andExpect(status().isCreated());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }

        verify(issueService).create(any(IssueDto.class));
    }
    @Test
    void testUpdateIssue() throws Exception {

        UUID issueId = UUID.fromString("aea3d132-dbf7-4d5f-a2b1-dd570bd32b43");
        IssueDto requestDto = mockIssueType();
        IssueDto responseDto = mockIssueType();

        doNothing().when(issueService).update(issueId,requestDto);
        mockMvc.perform(put("/lnf/sentinel/issues/{id}", issueId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isOk());

        verify(issueService).update(any(UUID.class), any(IssueDto.class));
    }

    @Test
    void testUpdateStatusForIssue() throws Exception {

        UUID issueId = UUID.fromString("aea3d132-dbf7-4d5f-a2b1-dd570bd32b43");
        IssueDto requestDto = mockIssueType();
        Map<String,Object> map=new HashMap<>();
        map.put("status","CLOSED");
        doNothing().when(issueService).changeStatus(issueId,map);
        mockMvc.perform(patch("/lnf/sentinel/issues/{id}/status", issueId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(map))).andExpect(status().isNoContent());

        verify(issueService).changeStatus(issueId,map);
    }
    @Test
    void testDeleteWithId() throws Exception {
        UUID issueId = UUID.fromString("aea3d132-dbf7-4d5f-a2b1-dd570bd32b43");
        IssueDto issueDto = mockIssueType();
        doNothing().when(issueService).deleteById(issueId);
        mockMvc.perform(delete("/lnf/sentinel/issues/{id}",issueId)).andExpect(status().isOk());
        verify(issueService).deleteById(issueId);

    }

    @Test
    void testListOfIssues() throws Exception {

        UUID tenantId = UUID.randomUUID();
        UUID assigneeId = UUID.randomUUID();

        IssueDto dto = new IssueDto();
        dto.setId(UUID.randomUUID());

        Page<IssueDto> page = new PageImpl<>(List.of(dto));

        when(issueService.list(
                eq("tenant1"),
                eq(tenantId),
                eq(IssueStatus.NEW),
                eq(Severity.S2_HIGH),
                eq(assigneeId),
                eq("test"),
                any(PageRequestDto.class)))
                .thenReturn(page);

        mockMvc.perform(get("/lnf/sentinel/issues")
                        .param("tenantName", "tenant1")
                        .param("tenantId", tenantId.toString())
                        .param("status", "NEW")
                        .param("severity", "S2_HIGH")
                        .param("assigneeId", assigneeId.toString())
                        .param("search", "test")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(issueService).list(
                eq("tenant1"),
                eq(tenantId),
                eq(IssueStatus.NEW),
                eq(Severity.S2_HIGH),
                eq(assigneeId),
                eq("test"),
                any(PageRequestDto.class));
    }


    private IssueDto mockIssueType() {
        UUID uuid= UUID.fromString("aea3d132-dbf7-4d5f-a2b1-dd570bd32b43");
        return createIssueType(uuid, "geting the errors in issue Links", UUID.fromString("aea3d132-dbf7-4d5f-a2b1-dd570bd32b43"));
    }

    private IssueDto createIssueType(UUID id, String summary, UUID tenantId) {
        IssueDto issueDto=new IssueDto();
        issueDto.setId(id);
        issueDto.setSummary(summary);
        issueDto.setTenantId(tenantId);
        return issueDto;
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


