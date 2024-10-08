package com.dpide.dpide.service;

import com.dpide.dpide.domain.Project;
import com.dpide.dpide.domain.ProjectRole;
import com.dpide.dpide.domain.ProjectUser;
import com.dpide.dpide.dto.ProjectDto;
import com.dpide.dpide.exception.*;
import com.dpide.dpide.repository.ProjectRepository;
import com.dpide.dpide.repository.ProjectUserRepository;
import com.dpide.dpide.user.domain.User;
import com.dpide.dpide.user.repository.UserRepository;
import com.dpide.dpide.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.naming.AuthenticationException;
import java.awt.color.ICC_Profile;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProjectServiceTest {
    ProjectService projectService;
    private final UserService userService = Mockito.mock(UserService.class);
    private final ProjectRepository projectRepository = Mockito.mock(ProjectRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final ProjectUserRepository projectUserRepository = Mockito.mock(ProjectUserRepository.class);
    private LocalDateTime at = LocalDateTime.now();
    private User user;
    private Project project;
    private String token = "Bearer 123";

    @BeforeEach
    public void setUpTest() {
        projectService = new ProjectService(userService, projectRepository, userRepository, projectUserRepository);

        user = User.builder()
                .id(1L)
                .email("dummy email")
                .password("dummy password")
                .nickname("dummy nickname")
                .createdAt(at)
                .updatedAt(at)
                .build();

        project = Project.builder()
                .id(1L)
                .name("dummy name")
                .description("dummy desc")
                .language("dummy lang")
                .createdAt(at)
                .updatedAt(at)
                .user(user)
                .files(new ArrayList<>()) // 빈 파일 리스트
                .chats(new ArrayList<>()) // 빈 채팅 리스트
                .projectUsers(new ArrayList<>()) // 빈 프로젝트 사용자 리스트
                .alarms(new ArrayList<>()) // 빈 알림 리스트
                .build();
    }

    @Test
    void createProject_Success() {
        // Given
        ProjectDto.CreationReq req = ProjectDto.CreationReq.builder()
                .name("dummy name")
                .description("dummy desc")
                .language("dummy lang")
                .build();
        req.setName("Test Project");

        when(userService.getAuthenticatedUser(any())).thenReturn(user);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        // When
        ProjectDto.ProjectInfoRes result = projectService.createProject(req, token);

        // Then
        Assertions.assertEquals(result.getId(), 1L);
        Assertions.assertEquals(result.getName(), "dummy name");
        Assertions.assertEquals(result.getDescription(), "dummy desc");
        Assertions.assertEquals(result.getLanguage(), "dummy lang");
        Assertions.assertEquals(result.getCreatedAt(), at);
        Assertions.assertEquals(result.getUpdatedAt(), at);
        Assertions.assertEquals(result.getUserId(), 1L);
    }



    @Test
    void createProject_UserNotFound() {
        // Given
        ProjectDto.CreationReq req = new ProjectDto.CreationReq();
        when(userService.getAuthenticatedUser(any())).thenReturn(user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> projectService.createProject(req, token));
    }

    @Test
    void getProjects_Success() {
        // Given
        Long userId = 1L;

        User user = User.builder().id(userId).build();
        Project project1 = Project.builder().id(1L).name("Project 1").description("Desc 1").language("Lang 1").user(user).build();
        Project project2 = Project.builder().id(2L).name("Project 2").description("Desc 2").language("Lang 2").user(user).build();

        List<Project> projects = List.of(project1, project2);

        when(userService.getAuthenticatedUser(token)).thenReturn(user);
        when(projectRepository.findByUserId(userId)).thenReturn(projects);
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));

        // When
        List<ProjectDto.ProjectInfoRes> result = projectService.getProjects(token);

        // Then
        Assertions.assertEquals(2, result.size());

        // Verify first project
        Assertions.assertEquals(1L, result.get(0).getId());
        Assertions.assertEquals("Project 1", result.get(0).getName());
        Assertions.assertEquals("Desc 1", result.get(0).getDescription());
        Assertions.assertEquals("Lang 1", result.get(0).getLanguage());

        // Verify second project
        Assertions.assertEquals(2L, result.get(1).getId());
        Assertions.assertEquals("Project 2", result.get(1).getName());
        Assertions.assertEquals("Desc 2", result.get(1).getDescription());
        Assertions.assertEquals("Lang 2", result.get(1).getLanguage());

        // Verify methods were called
        verify(userService).getAuthenticatedUser(token);
        verify(projectRepository).findByUserId(userId);
    }

    @Test
    void getInvitedProjects_Success() {
        // Given
        
        Long userId = 1L;

        User user = User.builder().id(userId).build();
        Project project1 = Project.builder().id(1L).name("Invited Project 1").description("Desc 1").language("Lang 1").user(user).createdAt(at).updatedAt(at).build();
        Project project2 = Project.builder().id(2L).name("Invited Project 2").description("Desc 2").language("Lang 2").user(user).createdAt(at).updatedAt(at).build();

        ProjectUser projectUser1 = ProjectUser.builder().user(user).project(project1).role(ProjectRole.PARTICIPANT).build();
        ProjectUser projectUser2 = ProjectUser.builder().user(user).project(project2).role(ProjectRole.PARTICIPANT).build();

        List<ProjectUser> invitedProjects = List.of(projectUser1, projectUser2);

        when(userService.getAuthenticatedUser(token)).thenReturn(user);
        when(projectUserRepository.findByUserIdAndRole(userId, ProjectRole.PARTICIPANT))
                .thenReturn(invitedProjects);
        when(userService.getAuthenticatedUser(token)).thenReturn(user);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        List<ProjectDto.ProjectInfoRes> result = projectService.getInvitedProjects(token);

        // Then
        Assertions.assertEquals(2, result.size());

        // Verify first invited project
        Assertions.assertEquals(1L, result.get(0).getId());
        Assertions.assertEquals("Invited Project 1", result.get(0).getName());

        // Verify second invited project
        Assertions.assertEquals(2L, result.get(1).getId());
        Assertions.assertEquals("Invited Project 2", result.get(1).getName());

        verify(userService).getAuthenticatedUser(token);
    }

    @Test
    void updateProject_Success() {
        // Given
        Long userId = 1L;
        Long projectId = 1L;
        Long projectUserId = 1L;

        User user = User.builder()
                .id(userId)
                .build();

        ProjectUser projectUser = ProjectUser.builder()
                .id(projectUserId)
                .role(ProjectRole.OWNER)
                .user(user)
                .createdAt(at)
                .build();

        Project project = Project.builder()
                .id(projectId)
                .name("Old Project Name")
                .description("Old Description")
                .user(user)
                .projectUsers(List.of(projectUser))
                .build();

        ProjectDto.UpdateReq req = ProjectDto.UpdateReq.builder()
                .name("Updated Project Name")
                .description("Updated Description")
                .build();

        when(userService.getAuthenticatedUser(token)).thenReturn(user);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        // When
        ProjectDto.ProjectInfoRes result = projectService.updateProject(projectId, req, token);

        // Then
        Assertions.assertEquals("Updated Project Name", result.getName());
        Assertions.assertEquals("Updated Description", result.getDescription());

        verify(userService).getAuthenticatedUser(token);
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void deleteProject_Success() {
        // Given

        Long userId = 1L;
        Long projectId = 1L;
        Long projectUserId = 1L;

        User user = User.builder()
                .id(userId)
                .build();

        ProjectUser projectUser = ProjectUser.builder()
                .id(projectUserId)
                .role(ProjectRole.OWNER)
                .user(user)
                .createdAt(at)
                .build();

        Project project = Project.builder()
                .id(projectId)
                .name("Old Project Name")
                .description("Old Description")
                .user(user)
                .projectUsers(List.of(projectUser))
                .build();

        when(userService.getAuthenticatedUser(token)).thenReturn(user);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        projectService.deleteProject(projectId, token);

        // Then
        verify(userService).getAuthenticatedUser(token);
        verify(projectRepository).delete(any(Project.class));
    }

    @Test
    void leaveProject_Success() {
        // Given
        when(userService.getAuthenticatedUser(any())).thenReturn(user);

        Project mockProject = new Project();
        mockProject.setId(1L);
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(mockProject));

        ProjectUser mockProjectUser = new ProjectUser();
        mockProjectUser.setProject(mockProject);
        when(projectUserRepository.findByProjectAndUserAndRole(any(), any(), any(ProjectRole.class)))
                .thenReturn(Optional.of(mockProjectUser));

        // When
        projectService.leaveProject(1L, token);

        // Then
        verify(projectUserRepository, times(1)).delete(any(ProjectUser.class));
    }

}