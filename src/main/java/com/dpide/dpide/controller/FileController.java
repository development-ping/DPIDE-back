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

@Slf4j
@RequiredArgsConstructor
@RestController
public class FileController {
    private final FileService fileService;

    @PostMapping("/projects/{projectId}/files")
    public ResponseEntity<FileDto.FileInfoRes> createFile(@PathVariable Long projectId,
                                                          @RequestBody FileDto.CreationReq req,
                                                          @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(fileService.createFile(projectId, req, token));
    }

    @DeleteMapping("/projects/{projectId}/files/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long projectId,
                                           @PathVariable Long fileId,
                                           @RequestHeader("Authorization") String token) {
        fileService.deleteFile(projectId, fileId, token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/projects/{projectId}/files/{fileId}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable Long projectId,
                                                       @PathVariable Long fileId,
                                                       @RequestHeader("Authorization") String token) throws IOException {
        InputStreamResource resource = fileService.getFileContent(projectId, fileId, token);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
    }

    @PutMapping("/projects/{projectId}/files/{fileId}")
    public ResponseEntity<FileDto.FileInfoRes> saveFile(@PathVariable Long projectId,
                                                        @PathVariable Long fileId,
                                                        @RequestPart("content") String name,
                                                        @RequestPart("content") MultipartFile content,
                                                        @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(fileService.updateFile(projectId, fileId, name, content, token));
    }
}