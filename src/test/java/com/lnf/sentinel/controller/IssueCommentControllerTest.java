package com.lnf.sentinel.controller;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lnf.dto.sentinel.IssueCommentDto;
import com.lnf.sentinel.BaseTestClass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class IssueCommentControllerTest extends BaseTestClass {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OAuth2AuthorizedClientManager authorizedClientManager;

    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @Test
    void testAddComment() throws Exception {

        UUID issueId=UUID.randomUUID();
        IssueCommentDto issueCommentDto=new IssueCommentDto();
        issueCommentDto.setId(issueId);

        doNothing().when(issueCommentService).addComment(eq(issueId),any(IssueCommentDto.class));
        mockMvc.perform(post("/lnf/sentinel/issues/comments/{issueId}",issueId)
                .contentType(APPLICATION_JSON)
                .content(asJsonString(issueCommentDto))).andExpect(status().isOk());

        verify(issueCommentService).addComment(eq(issueId),any(IssueCommentDto.class));
    }

    @Test
    void listOfIssueComments() throws Exception{
        UUID issueId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        IssueCommentDto issueCommentDto=new IssueCommentDto();
        issueCommentDto.setId(issueId);


        Map<String, Object> comment = new HashMap<>();
        comment.put("IssueId", issueId);
        comment.put("UserId", userId);
        comment.put("FullName", "John Doe");
        comment.put("createdTime", LocalDateTime.now());

        List<Map<String, Object>> comments = List.of(comment);

        List<IssueCommentDto> list=List.of(issueCommentDto);

        when(issueCommentService.listComments(issueId)).thenReturn(comments);

        mockMvc.perform(get("/lnf/sentinel/issues/comments/{issueId}",issueId)
                        .contentType(APPLICATION_JSON))
                        .andExpect(status().isOk());
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
