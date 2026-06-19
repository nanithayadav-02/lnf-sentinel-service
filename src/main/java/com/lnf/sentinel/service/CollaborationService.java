package com.lnf.sentinel.service;

import com.lnf.exception.LnFBadRequestException;
import com.lnf.exception.LnFEntityNotFoundException;
import com.lnf.sentinel.domain.*;
import com.lnf.sentinel.dto.*;
import com.lnf.sentinel.repository.*;
import com.lnf.sentinel.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Comments, cross-issue links, watchers and attachments. All tenant-scoped.
 */
@Service
@RequiredArgsConstructor
public class CollaborationService {

    private final IssueRepository issueRepository;
    private final IssueCommentRepository commentRepository;
    private final IssueLinkRepository linkRepository;
    private final IssueWatcherRepository watcherRepository;
    private final IssueAttachmentRepository attachmentRepository;

    // ---- comments ---------------------------------------------------------

    @Transactional
    public CommentResponse addComment(Long issueId, CreateCommentRequest req) {
        requireIssue(issueId);
        IssueComment c = new IssueComment();
        c.setIssueId(issueId);
        c.setAuthorId(req.authorId());
        c.setBody(req.body());
        c.setInternal(req.internal() == null || req.internal());
        return CommentResponse.from(commentRepository.save(c));
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> listComments(Long issueId) {
        requireIssue(issueId);
        return commentRepository.findByIssueIdOrderByCreatedAtAsc(issueId)
                .stream().map(CommentResponse::from).toList();
    }

    // ---- links ------------------------------------------------------------

    @Transactional
    public LinkResponse createLink(Long sourceIssueId, CreateLinkRequest req) {
        Issue source = requireIssue(sourceIssueId);
        if (sourceIssueId.equals(req.targetIssueId())) {
            throw new LnFBadRequestException("An issue cannot be linked to itself");
        }
        // Target must exist and be visible to the current tenant.
        requireIssue(req.targetIssueId());
        if (linkRepository.existsBySourceIssueIdAndTargetIssueIdAndLinkType(
                sourceIssueId, req.targetIssueId(), req.linkType())) {
            throw new LnFBadRequestException("That link already exists");
        }
        IssueLink link = new IssueLink();
        link.setSourceIssueId(source.getId());
        link.setTargetIssueId(req.targetIssueId());
        link.setLinkType(req.linkType());
        link.setCreatedBy(req.createdBy());
        return LinkResponse.from(linkRepository.save(link), sourceIssueId);
    }

    @Transactional(readOnly = true)
    public List<LinkResponse> listLinks(Long issueId) {
        requireIssue(issueId);
        List<LinkResponse> out = new ArrayList<>();
        linkRepository.findBySourceIssueId(issueId).forEach(l -> out.add(LinkResponse.from(l, issueId)));
        linkRepository.findByTargetIssueId(issueId).forEach(l -> out.add(LinkResponse.from(l, issueId)));
        return out;
    }

    @Transactional
    public void deleteLink(Long linkId) {
        IssueLink link = linkRepository.findById(linkId)
                .orElseThrow(() -> new LnFEntityNotFoundException("Link not found: " + linkId));
        // Tenant-scope: the source issue must be visible to the caller.
        requireIssue(link.getSourceIssueId());
        linkRepository.delete(link);
    }

    // ---- watchers (idempotent) -------------------------------------------

    @Transactional
    public WatcherResponse addWatcher(Long issueId, Long userId) {
        requireIssue(issueId);
        return WatcherResponse.from(
                watcherRepository.findByIssueIdAndUserId(issueId, userId)
                        .orElseGet(() -> {
                            IssueWatcher w = new IssueWatcher();
                            w.setIssueId(issueId);
                            w.setUserId(userId);
                            return watcherRepository.save(w);
                        }));
    }

    @Transactional
    public void removeWatcher(Long issueId, Long userId) {
        requireIssue(issueId);
        watcherRepository.findByIssueIdAndUserId(issueId, userId)
                .ifPresent(watcherRepository::delete);
    }

    @Transactional(readOnly = true)
    public List<WatcherResponse> listWatchers(Long issueId) {
        requireIssue(issueId);
        return watcherRepository.findByIssueId(issueId)
                .stream().map(WatcherResponse::from).toList();
    }

    // ---- attachments ------------------------------------------------------

    @Transactional
    public AttachmentResponse addAttachment(Long issueId, CreateAttachmentRequest req) {
        requireIssue(issueId);
        IssueAttachment a = new IssueAttachment();
        a.setIssueId(issueId);
        a.setFileName(req.fileName());
        a.setStorageKey(req.storageKey());
        a.setContentType(req.contentType());
        a.setSizeBytes(req.sizeBytes());
        a.setUploadedBy(req.uploadedBy());
        return AttachmentResponse.from(attachmentRepository.save(a));
    }

    @Transactional(readOnly = true)
    public List<AttachmentResponse> listAttachments(Long issueId) {
        requireIssue(issueId);
        return attachmentRepository.findByIssueIdOrderByCreatedAtAsc(issueId)
                .stream().map(AttachmentResponse::from).toList();
    }

    // ---- helper -----------------------------------------------------------

    /**
     * Load an issue scoped to the pinned tenant; 404 (never leak) on cross-tenant access.
     */
    private Issue requireIssue(Long issueId) {
        if (TenantContext.isSet()) {
            return issueRepository.findByIdAndTenantId(issueId, TenantContext.getTenantId())
                    .orElseThrow(() -> new LnFEntityNotFoundException("Issue not found: " + issueId));
        }
        return issueRepository.findById(issueId)
                .orElseThrow(() -> new LnFEntityNotFoundException("Issue not found: " + issueId));
    }

}
