package com.lnf.sentinel.repository;

import com.lnf.sentinel.AbstractIntegrationTest;
import com.lnf.sentinel.domain.Issue;
import com.lnf.sentinel.domain.enums.IssueStatus;
import com.lnf.sentinel.domain.enums.Severity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.util.Optional;

import static com.lnf.sentinel.service.IssueSpecifications.severity;
import static com.lnf.sentinel.service.IssueSpecifications.tenantId;
import static org.assertj.core.api.Assertions.assertThat;

class IssueRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    IssueRepository issueRepository;

    private Issue persistIssue(long tenant, Severity sev) {
        Issue i = new Issue();
        i.setIssueKey("PRD-" + issueRepository.nextIssueKeyNumber());
        i.setTenantId(tenant);
        i.setTitle("Checkout 500s");
        i.setSeverity(sev);
        i.setStatus(IssueStatus.NEW);
        i.setDetectedAt(OffsetDateTime.now());
        i.setSlaDueAt(OffsetDateTime.now().plus(sev.slaTarget()));
        return issueRepository.save(i);
    }

    @Test
    void persistsAndFindsByKey() {
        Issue saved = persistIssue(ACME, Severity.S1_CRITICAL);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();

        Optional<Issue> byKey = issueRepository.findByIssueKey(saved.getIssueKey());
        assertThat(byKey).isPresent();
        assertThat(byKey.get().getTitle()).isEqualTo("Checkout 500s");
    }

    @Test
    void findByIdAndTenantIdEnforcesTenant() {
        Issue saved = persistIssue(ACME, Severity.S2_HIGH);
        assertThat(issueRepository.findByIdAndTenantId(saved.getId(), ACME)).isPresent();
        assertThat(issueRepository.findByIdAndTenantId(saved.getId(), GLOBEX)).isEmpty();
    }

    @Test
    void specificationsFilter() {
        Issue s1 = persistIssue(ACME, Severity.S1_CRITICAL);
        persistIssue(ACME, Severity.S4_LOW);

        var result = issueRepository.findAll(tenantId(ACME).and(severity(Severity.S1_CRITICAL)));
        assertThat(result).extracting(Issue::getId).contains(s1.getId());
        assertThat(result).allMatch(i -> i.getSeverity() == Severity.S1_CRITICAL);
    }

    @Test
    void issueKeysAreSequential() {
        long a = issueRepository.nextIssueKeyNumber();
        long b = issueRepository.nextIssueKeyNumber();
        assertThat(b).isGreaterThan(a);
    }
}
