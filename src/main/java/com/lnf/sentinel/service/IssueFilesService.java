package com.lnf.sentinel.service;

import com.lnf.dto.sentinel.FileDto;
import com.lnf.exception.LnFEntityNotFoundException;
import com.lnf.exception.LnFException;
import com.lnf.sentinel.repository.IssueRepository;
import com.lnf.service.file.FileFolderService;
import com.lnf.service.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class IssueFilesService {

    private final FileService fileService;
    private final FileFolderService fileFolderService;
    private final IssueRepository issueRepository;
    public static final String FILES = "files";
    public static final String S_S_S = "%s/%s/%s/";
    @Value("${aws.s3.bucket.folderName}")
    private String folderName;

    public List<FileDto> findByIssueId(UUID issueId) {
        String filePath = S_S_S.formatted(folderName, issueId, FILES);
        List<com.lnf.dto.file.FileDto> files = fileFolderService.findFiles(filePath);
        return setEmployeeFileDto(issueId, files);
    }

    private List<FileDto> setEmployeeFileDto(UUID issueId, List<com.lnf.dto.file.FileDto> files) {
        List<FileDto> fileDtos = new ArrayList<>();
        files.forEach(file -> {
            String fileName = StringUtils.substringAfterLast(file.getFileName(), "/");
            String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/lnf/sentinel/issues/%s/files/%s".formatted(issueId, fileName))
                    .toUriString();
            FileDto fileDto = new FileDto();
            fileDto.setName(fileName);
            fileDto.setUrl(downloadURL);
            fileDto.setSize(file.getFileSize());
            fileDto.setCreatedTime(file.getLastModified());
            fileDtos.add(fileDto);
        });
        return fileDtos;
    }

    public ResponseEntity<byte[]> findById(UUID issueId, String fileName) {
        try {
            String filePath = "%s/%s/%s/%s".formatted(folderName, issueId, FILES, fileName);
            return fileService.findFileContent(filePath);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to get file for issue[%s]".formatted(issueId);
            throw new LnFException(errorMessage, e);
        }
    }

    public void create(UUID issueId, MultipartFile[] files) {
        for (MultipartFile file : files) {
            searchForIssueId(issueId);
            String folder = S_S_S.formatted(folderName, issueId, FILES);
            uploadFile(folder, file);
        }
    }

    private void searchForIssueId(UUID id) {
        issueRepository.findById(id)
                .orElseThrow(() -> new LnFEntityNotFoundException("Issue not found: " + id));
    }

    private void uploadFile(String folder, MultipartFile file) {
        String filePath = fileService.uploadFile(folder, file);
        log.debug("File uploaded successfully {}", filePath);
    }

    public void deleteById(UUID issueId, String fileName) {
        String filePath = "%s/%s/%s/%s".formatted(folderName, issueId, FILES, fileName);
        List<String> filePaths = Collections.singletonList(filePath);
        fileService.delete(filePaths);
        log.debug("issue files deleted successfully");
    }

}
