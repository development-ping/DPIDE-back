package com.dpide.dpide.controller;

import com.dpide.dpide.dto.FileDto;
import com.dpide.dpide.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class FileController {
    private final FileService fileService;

    @PostMapping("/projects/{projectId}/files")
    public ResponseEntity<FileDto.FileInfoRes> createFile(@PathVariable Long projectId,
                                                          @RequestBody FileDto.CreationReq req,
                                                          @RequestHeader("Authorization") String token) {
        log.info("CALL: FileController.createFile");
        return ResponseEntity.ok(fileService.createFile(projectId, req, token));
    }

    @DeleteMapping("/projects/{projectId}/files/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long projectId,
                                           @PathVariable Long fileId,
                                           @RequestHeader("Authorization") String token) {
        log.info("CALL: FileController.deleteFile");
        fileService.deleteFile(projectId, fileId, token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/projects/{projectId}/files/{fileId}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable Long projectId,
                                                       @PathVariable Long fileId,
                                                       @RequestHeader("Authorization") String token) throws IOException {
        log.info("CALL: FileController.getFile");
        InputStreamResource resource = fileService.getFileContent(projectId, fileId, token);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
    }

    @PutMapping("/projects/{projectId}/files/{fileId}")
    public ResponseEntity<FileDto.FileInfoRes> saveFile(@PathVariable Long projectId,
                                                        @PathVariable Long fileId,
                                                        @RequestPart("content") MultipartFile content,
                                                        @RequestHeader("Authorization") String token) {
        log.info("CALL: FileController.saveFile");
        return ResponseEntity.ok(fileService.updateFile(projectId, fileId, content, token));
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<FileDto.FileTreeListRes> getFileTree(@PathVariable Long projectId,
                                                               @RequestHeader("Authorization") String token) {
        log.info("CALL: FileController.getFileTree");
        return ResponseEntity.ok(fileService.getFileTree(projectId, token));
    }

    @PostMapping("/projects/{projectId}/files/{fileId}")
    public ResponseEntity<String> executeFile(@PathVariable Long projectId,
                                              @PathVariable Long fileId,
                                              @RequestBody Map<String, String> userInput,
                                              @RequestHeader("Authorization") String token) {
        log.info("CALL: FileController.executeFile");
        String executionResult = fileService.executeFile(projectId, fileId, userInput, token);
        return ResponseEntity.ok(executionResult);
    }
}