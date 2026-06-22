package com.lnf.sentinel.converter;

import com.lnf.dto.sentinel.IssueAttachmentDto;
import com.lnf.sentinel.model.IssueAttachment;

public class IssuseAttachmentConverter {

    private IssuseAttachmentConverter(){}

    public IssueAttachmentDto toTransportModel(IssueAttachment entity){
        if(entity==null){
            return null;
        }
        return IssueAttachmentDto.builder()
                .issueId(entity.getIssueId())
                .contentType(entity.getContentType())
                .fileName(entity.getFileName())
                .sizeBytes(entity.getSizeBytes())
                .storageKey(entity.getStorageKey())
                .uploadedBy(entity.getUploadedBy())
                .build();
    }

    public IssueAttachment toEntityModel(IssueAttachment issueAttachment,IssueAttachmentDto dto){
        issueAttachment.setIssueId(dto.getIssueId());
        issueAttachment.setFileName(dto.getFileName());
        issueAttachment.setContentType(dto.getContentType());
        issueAttachment.setUploadedBy(dto.getUploadedBy());
        issueAttachment.setStorageKey(dto.getStorageKey());
        issueAttachment.setSizeBytes(dto.getSizeBytes());
        return issueAttachment;
    }
}
