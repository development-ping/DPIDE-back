package com.dpide.dpide.controller;

import com.dpide.dpide.dto.FileDto;
import com.dpide.dpide.service.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;


import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.BDDMockito.given;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@AutoConfigureMockMvc
@WebMvcTest(FileController.class)
class FileControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private FileService fileService;
    private String token = "Bearer 123";

    @Test
    @WithMockUser
    void createFile_Success() throws Exception {
        Long projectId = 1L;
        Long fileId = 1L;
        FileDto.CreationReq creationReq = FileDto.CreationReq.builder()
                .name("dummy name")
                .extension("dummy ext")
                .path("dummy path")
                .parentId(fileId)
                .build();

        FileDto.FileInfoRes fileInfoRes = FileDto.FileInfoRes.builder()
                .id(1L)
                .name("dummy name")
                .extension("dummy ext")
                .projectId(projectId)
                .parentId(fileId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        String content = objectMapper.writeValueAsString(creationReq);

        // response 변수 대신 fileInfoRes 사용
        given(fileService.createFile(eq(projectId), eq(creationReq), eq(token))).willReturn(fileInfoRes);

        mockMvc.perform(post("/projects/{projectId}/files", projectId)
                        .header("Authorization", token)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());

        // eq(creationReq)를 사용하여 정확한 객체 비교
        //verify(fileService).createFile(eq(projectId), eq(creationReq), eq(token));
    }

    @Test
    @WithMockUser
    void deleteFile_Success() throws Exception {
        Long projectId = 1L;
        Long fileId = 1L;

        mockMvc.perform(delete("/projects/{projectId}/files/{fileId}", projectId, fileId, token)
                        .header("Authorization", token)
        )//.with(csrf()))
                .andExpect(status().isOk());

        verify(fileService).deleteFile(projectId, fileId, token);
    }

    @Test
    @WithMockUser
    void getFile_Success() throws Exception {
        Long projectId = 1L;
        Long fileId = 1L;
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream("File content".getBytes()));

        given(fileService.getFileContent(projectId, fileId, token)).willReturn(resource);

        mockMvc.perform(get("/projects/{projectId}/files/{fileId}", projectId, fileId)
                        .header("Authorization", token)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(result ->
                        result.getResponse().getContentType().equals(MediaType.TEXT_PLAIN_VALUE));

        verify(fileService).getFileContent(projectId, fileId, token);
        
    }
    /* =====
    @Test
    @WithMockUser
    void saveFile_Success() throws Exception {
        Long projectId = 1L;
        Long fileId = 1L;
        FileDto.FileInfoRes fileInfoRes = FileDto.FileInfoRes.builder()
                .id(1L)
                .name("dummy name")
                .extension("dummy ext")
                .projectId(projectId)
                .parentId(fileId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Create a mock MultipartFile
        MockMultipartFile mockFile = new MockMultipartFile("content",
                "testFile.txt", // 파일 이름
                "text/plain", // MIME 타입
                "File content".getBytes()); // 실제 파일 내용

        given(fileService.updateFile(any(), any(), any(MultipartFile.class), any())).willReturn(fileInfoRes);

        mockMvc.perform(multipart("/projects/{projectId}/files/{fileId}", projectId, fileId)
                        .file(mockFile)
                        .header("Authorization", token)
                        .with(csrf()))
                        //.contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(fileService).updateFile(projectId, fileId, mockFile, token);
    }
    */
    @Test
    @WithMockUser
    void getFileTree_Success() throws Exception {
        Long projectId = 1L;
        FileDto.FileTreeListRes response = new FileDto.FileTreeListRes(); // Populate with mock response

        when(fileService.getFileTree(anyLong(), any())).thenReturn(response);

        mockMvc.perform(get("/projects/{projectId}", projectId)
                        .header("Authorization", token)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(fileService).getFileTree(projectId, token);
    }

    @Test
    @WithMockUser
    void executeFile_Success() throws Exception {
        Long projectId = 1L;
        Long fileId = 1L;
        Map<String, String> userInput = new HashMap<>();
        userInput.put("key", "value");
        String executionResult = "Execution result";

        when(fileService.executeFile(anyLong(), anyLong(), any(), any())).thenReturn(executionResult);

        mockMvc.perform(post("/projects/{projectId}/files/{fileId}", projectId, fileId)
                        .header("Authorization", token)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"key\": \"value\"}")) // Replace with valid JSON body
                .andExpect(status().isOk());

        verify(fileService).executeFile(projectId, fileId, userInput, token);
    }
}