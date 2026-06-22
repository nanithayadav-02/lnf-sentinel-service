package com.lnf.sentinel.service;

import com.lnf.exception.LnFBadRequestException;
import com.lnf.sentinel.AbstractIntegrationTest;
import com.lnf.sentinel.model.enums.*;
import com.lnf.sentinel.tenant.TenantContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CollaborationServiceTest extends AbstractIntegrationTest {

    @Autowired
    IssueService issueService;
    @Autowired
    CollaborationService collaboration;

    private Long a;
    private Long b;

    @BeforeEach
    void setUp() {
        TenantContext.setTenantId(ACME);
        a = newIssue("Primary");
        b = newIssue("Related");
    }

    private Long newIssue(String title) {
        return issueService.create(new CreateIssueRequest(
                title, null, null, Severity.S3_MEDIUM, Priority.P3, Category.BUG,
                Environment.PRODUCTION, "svc", ENG1, ENG1, OffsetDateTime.now())).id();
    }

    @Test
    void rejectsSelfLink() {
        assertThatThrownBy(() -> collaboration.createLink(a,
                new CreateLinkRequest(a, LinkType.RELATES_TO, ENG1)))
                .isInstanceOf(LnFBadRequestException.class);
    }

    @Test
    void rejectsDuplicateLink() {
        collaboration.createLink(a, new CreateLinkRequest(b, LinkType.BLOCKS, ENG1));
        assertThatThrownBy(() -> collaboration.createLink(a,
                new CreateLinkRequest(b, LinkType.BLOCKS, ENG1)))
                .isInstanceOf(LnFBadRequestException.class);
    }

    @Test
    void linkIsVisibleFromBothSidesWithInverseDirection() {
        collaboration.createLink(a, new CreateLinkRequest(b, LinkType.BLOCKS, ENG1));

        var fromA = collaboration.listLinks(a);
        var fromB = collaboration.listLinks(b);
        assertThat(fromA).hasSize(1);
        assertThat(fromA.get(0).direction()).isEqualTo(LinkType.BLOCKS);
        assertThat(fromB).hasSize(1);
        assertThat(fromB.get(0).direction()).isEqualTo(LinkType.BLOCKED_BY);
    }

    @Test
    void commentsDefaultToInternal() {
        CommentResponse c = collaboration.addComment(a, new CreateCommentRequest("ops note", ENG1, null));
        assertThat(c.internal()).isTrue();
        assertThat(collaboration.listComments(a)).hasSize(1);

        CommentResponse tenantVisible = collaboration.addComment(a,
                new CreateCommentRequest("visible to tenant", ENG1, false));
        assertThat(tenantVisible.internal()).isFalse();
    }

    @Test
    void watchersAreIdempotent() {
        collaboration.addWatcher(a, ENG1);
        collaboration.addWatcher(a, ENG1); // idempotent
        assertThat(collaboration.listWatchers(a)).hasSize(1);

        collaboration.removeWatcher(a, ENG1);
        assertThat(collaboration.listWatchers(a)).isEmpty();
        collaboration.removeWatcher(a, ENG1); // idempotent, no error
    }

    @Test
    void attachmentMetadataIsStored() {
        AttachmentResponse att = collaboration.addAttachment(a,
                new CreateAttachmentRequest("trace.log", "s3://bucket/trace.log", "text/plain", 2048L, ENG1));
        assertThat(att.id()).isNotNull();
        assertThat(collaboration.listAttachments(a)).hasSize(1);
    }
}
