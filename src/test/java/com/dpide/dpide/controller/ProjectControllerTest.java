package com.dpide.dpide.controller;

import com.dpide.dpide.dto.ProjectDto;
import com.dpide.dpide.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
@AutoConfigureMockMvc
@WebMvcTest(ProjectController.class)
class ProjectControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private ProjectService projectService;

    private final String token = "Bearer someToken";

    @Test
    @DisplayName("POST /projects success")
    @WithMockUser
    void createProject_Success() throws Exception {
        // Given
        ProjectDto.CreationReq creationReq = ProjectDto.CreationReq.builder()
                .name("dummy project")
                .description("dummy desc") // 필요한 필드로 대체
                .language("dummy language")
                .build();

        ProjectDto.ProjectInfoRes projectInfoRes = ProjectDto.ProjectInfoRes.builder()
                .id(1L)
                .name("dummy name")
                .description("dummy desc")
                .language("dummy language")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .userId(1L)
                .build();

        String content = objectMapper.writeValueAsString(creationReq);
        given(projectService.createProject(any(ProjectDto.CreationReq.class), eq(token)))
                .willReturn(projectInfoRes);

        // When & Then
        mockMvc.perform(post("/projects")
                        .header("Authorization", token)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)) // JSON 형식의 요청 본문
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /projects success")
    @WithMockUser
    void getProjects_Success() throws Exception {
        // Given
        List<ProjectDto.ProjectInfoRes> projects = Arrays.asList(
                ProjectDto.ProjectInfoRes.builder().id(1L).name("Project 1").build(),
                ProjectDto.ProjectInfoRes.builder().id(2L).name("Project 2").build()
        );

        given(projectService.getProjects(eq(token))).willReturn(projects);

        // When & Then
        mockMvc.perform(get("/projects")
                        .header("Authorization", token)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /projects/invited success")
    @WithMockUser
    void getInvitedProjects_Success() throws Exception {
        // Given
        List<ProjectDto.ProjectInfoRes> projects = Arrays.asList(
                ProjectDto.ProjectInfoRes.builder().id(1L).name("Invited Project 1").build(),
                ProjectDto.ProjectInfoRes.builder().id(2L).name("Invited Project 2").build()
        );

        given(projectService.getInvitedProjects(eq(token))).willReturn(projects);

        // When & Then
        mockMvc.perform(get("/projects/invited")
                        .header("Authorization", token)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /projects/{projectId} success")
    @WithMockUser
    void updateProject_Success() throws Exception {
        // Given
        Long projectId = 1L;
        ProjectDto.UpdateReq updateReq = ProjectDto.UpdateReq.builder()
                .name("Updated Project")
                .description("Updated Description") // 필요한 필드로 대체
                .build();

        ProjectDto.ProjectInfoRes updatedProjectInfo = ProjectDto.ProjectInfoRes.builder()
                .id(projectId)
                .name("Updated Project")
                .description("Updated Description") // 필요한 필드로 대체
                .build();

        String content = objectMapper.writeValueAsString(updateReq);
        given(projectService.updateProject(eq(projectId), any(ProjectDto.UpdateReq.class), eq(token)))
                .willReturn(updatedProjectInfo);

        // When & Then
        mockMvc.perform(put("/projects/{projectId}", projectId)
                        .header("Authorization", token)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)) // JSON 형식의 요청 본문
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /projects/{projectId} success")
    @WithMockUser
    void deleteProject_Success() throws Exception {
        // Given
        Long projectId = 1L;

        // When & Then
        mockMvc.perform(delete("/projects/{projectId}", projectId)
                        .header("Authorization", token)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /projects/{projectId}/leave success")
    @WithMockUser
    void leaveProject_Success() throws Exception {
        // Given
        Long projectId = 1L;

        // When & Then
        mockMvc.perform(post("/projects/{projectId}/leave", projectId)
                        .header("Authorization", token)
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}