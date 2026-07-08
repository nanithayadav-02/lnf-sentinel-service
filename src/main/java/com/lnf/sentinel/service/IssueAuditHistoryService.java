package com.lnf.sentinel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lnf.sentinel.model.IssueAuditHistory;
import com.lnf.sentinel.repository.IssueAuditHistoryRepository;
import com.lnf.sentinel.repository.IssueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class IssueAuditHistoryService {

    private final IssueAuditHistoryRepository issueAuditHistoryRepository;
    private final IssueRepository issueRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void log(String module,
                    String action,
                    UUID entityId,
                    String details,
                    Object payload) {

        IssueAuditHistory audit = new IssueAuditHistory();

        audit.setId(UUID.randomUUID());
        audit.setModule(module);
        audit.setAction(action);
        audit.setEntityId(entityId);

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = "SYSTEM";

        if (authentication != null &&
                authentication.getPrincipal() instanceof Jwt jwt) {

            username = jwt.getClaimAsString("preferred_username");
        }

        audit.setPerformedBy(username);

        audit.setDetails(details);

        audit.setInputPayload(objectMapper.valueToTree(payload));

        issueAuditHistoryRepository.save(audit);
    }

    public List<Map<String, Object>> getIssueAuditHistory() {

        List<Object[]> list = issueAuditHistoryRepository.findIssueAuditHistory();
        return list.stream().filter(Objects::nonNull).map(values -> {

            Map<String, Object> map = new HashMap<>();
            map.put("issueId", values[0]);
            map.put("issueKey", values[1]);
            map.put("action", values[2]);
            map.put("details", values[3]);
            map.put("createdTime", values[4]);
            return map;
        }).toList();
    }

}
