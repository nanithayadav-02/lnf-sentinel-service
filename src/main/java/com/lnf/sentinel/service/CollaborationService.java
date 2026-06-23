package com.lnf.sentinel.service;

import com.lnf.dto.sentinel.IssueAttachmentDto;
import com.lnf.dto.sentinel.IssueCommentDto;
import com.lnf.dto.sentinel.IssueLinkDto;
import com.lnf.dto.sentinel.IssueWatcherDto;
import com.lnf.exception.LnFBadRequestException;
import com.lnf.exception.LnFEntityNotFoundException;
import com.lnf.sentinel.converter.IssueAttachmentConverter;
import com.lnf.sentinel.converter.IssueCommentConverter;
import com.lnf.sentinel.converter.IssueLinkConverter;
import com.lnf.sentinel.converter.IssueWatcherConverter;
import com.lnf.sentinel.model.*;
import com.lnf.sentinel.model.enums.LinkType;
import com.lnf.sentinel.repository.*;
import com.lnf.sentinel.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public IssueCommentDto  addComment(UUID issueId, IssueCommentDto req) {
        requireIssue(issueId);
        IssueComment comment = new IssueComment();
        comment.setIssueId(issueId);
        comment.setAuthorId(req.getAuthorId());
        comment.setBody(req.getBody());
        comment.setInternal(req.isInternal());
        commentRepository.save(comment);
        return IssueCommentConverter.toTransportModel(comment);
    }

    @Transactional(readOnly = true)
    public List<IssueCommentDto> listComments(UUID issueId) {
        requireIssue(issueId);
        return commentRepository.findByIssueId(issueId)
                .stream().map(IssueCommentConverter::toTransportModel).toList();
    }

    // ---- links ------------------------------------------------------------

    @Transactional
    public IssueLinkDto createLink(UUID sourceIssueId, IssueLinkDto req) {
        Issue source = requireIssue(sourceIssueId);
        if (sourceIssueId.equals(req.getTargetIssueId())) {
            throw new LnFBadRequestException("An issue cannot be linked to itself");
        }
        // Target must exist and be visible to the current tenant.
        requireIssue(req.getTargetIssueId());
        if (linkRepository.existsBySourceIssueIdAndTargetIssueIdAndLinkType(
                sourceIssueId, req.getTargetIssueId(), LinkType.valueOf(req.getLinkType()))) {
            throw new LnFBadRequestException("That link already exists");
        }
        IssueLink link = new IssueLink();
        link.setSourceIssueId(source.getId());
        link.setTargetIssueId(req.getTargetIssueId());
        link.setLinkType(LinkType.valueOf(req.getLinkType()));
        link.setCreatedBy(req.getCreatedBy());
        linkRepository.save(link);
        return IssueLinkConverter.toDto(link);
    }

    @Transactional(readOnly = true)
    public List<IssueLinkDto> listLinks(UUID issueId) {
        requireIssue(issueId);
        List<IssueLinkDto> out = new ArrayList<>();
        linkRepository.findBySourceIssueId(issueId).forEach(l -> out.add(IssueLinkConverter.toDto(l)));
        linkRepository.findByTargetIssueId(issueId).forEach(l -> out.add(IssueLinkConverter.toDto(l)));
        return out;
    }

    @Transactional
    public void deleteLink(UUID linkId) {
        IssueLink link = linkRepository.findById(linkId)
                .orElseThrow(() -> new LnFEntityNotFoundException("Link not found: " + linkId));
        // Tenant-scope: the source issue must be visible to the caller.
        requireIssue(link.getSourceIssueId());
        linkRepository.delete(link);
    }

    // ---- watchers (idempotent) -------------------------------------------

    @Transactional
    public IssueWatcherDto addWatcher(UUID issueId, UUID userId) {
        requireIssue(issueId);
        IssueWatcher w = watcherRepository.findByIssueIdAndUserId(issueId, userId)
                .orElseGet(() -> {
                    IssueWatcher watcher=new IssueWatcher();
                    watcher.setIssueId(issueId);
                    watcher.setUserId(userId);
                    return watcherRepository.save(watcher);
                });
        return IssueWatcherConverter.toDto(w);

    }

    @Transactional
    public void removeWatcher(UUID issueId, UUID userId) {
        requireIssue(issueId);
        watcherRepository.findByIssueIdAndUserId(issueId, userId)
                .ifPresent(watcherRepository::delete);
    }

    @Transactional(readOnly = true)
    public List<IssueWatcherDto> listWatchers(UUID issueId) {
        requireIssue(issueId);
        return watcherRepository.findByIssueId(issueId)
                .stream().map(IssueWatcherConverter::toDto).toList();
    }

    // ---- attachments ------------------------------------------------------

    @Transactional
    public IssueAttachmentDto addAttachment(UUID issueId, IssueAttachmentDto req) {
        requireIssue(issueId);
        IssueAttachment attachment = new IssueAttachment();
        attachment.setIssueId(req.getIssueId());
        attachment.setFileName(req.getFileName());
        attachment.setStorageKey(req.getStorageKey());
        attachment.setContentType(req.getContentType());
        attachment.setSizeBytes(req.getSizeBytes());
        attachment.setUploadedBy(req.getUploadedBy());
        attachmentRepository.save(attachment);
        return IssueAttachmentConverter.toTransportModel(attachment);
    }

    @Transactional(readOnly = true)
    public List<IssueAttachmentDto> listAttachments(UUID issueId) {
        requireIssue(issueId);
        return attachmentRepository.findByIssueId(issueId)
                .stream().map(IssueAttachmentConverter::toTransportModel).toList();
    }

    // ---- helper -----------------------------------------------------------

    /**
     * Load an issue scoped to the pinned tenant; 404 (never leak) on cross-tenant access.
     */
    private Issue requireIssue(UUID issueId) {
        if (TenantContext.isSet()) {
            return issueRepository.findByIdAndTenantId(issueId, TenantContext.getTenantId())
                    .orElseThrow(() -> new LnFEntityNotFoundException("Issue not found: " + issueId));
        }
        return issueRepository.findById(issueId)
                .orElseThrow(() -> new LnFEntityNotFoundException("Issue not found: " + issueId));
    }

}
