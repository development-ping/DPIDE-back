package com.dpide.dpide.service;

import com.dpide.dpide.domain.File;
import com.dpide.dpide.domain.Project;
import com.dpide.dpide.domain.ProjectRole;
import com.dpide.dpide.domain.ProjectUser;
import com.dpide.dpide.dto.FileDto;
import com.dpide.dpide.repository.FileRepository;
import com.dpide.dpide.repository.ProjectRepository;
import com.dpide.dpide.user.domain.User;
import com.dpide.dpide.user.repository.UserRepository;
import com.dpide.dpide.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

import com.dpide.dpide.exception.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

class FileServiceTest {
    /*
    private FileService fileService;
    private FileRepository fileRepository = Mockito.mock(FileRepository.class);
    private UserService userService = Mockito.mock(UserService.class);
    private ProjectRepository projectRepository = Mockito.mock(ProjectRepository.class);
    private UserRepository userRepository = Mockito.mock(UserRepository.class);

    private User user;
    private Project project;
    private ProjectUser projectUser;
    private String token = "Bearer 123";
    private LocalDateTime createAt = LocalDateTime.now();
    private LocalDateTime updateAt = LocalDateTime.now();

    @BeforeEach
    public void setUp() {
        fileService = new FileService(userService, fileRepository, userRepository, projectRepository);

        user = User.builder()
                .id(1L)
                .email("dummy email")
                .nickname("dummy nickname")
                .password("dummy password")
                .createdAt(createAt)
                .updatedAt(updateAt)
                .build();

        project = Project.builder()
                .name("dummy name")
                .description("dummy desc")
                .language("dummy language")
                .createdAt(createAt)
                .updatedAt(updateAt)
                .user(user)
                .files(new ArrayList<>()) // 빈 파일 리스트
                .chats(new ArrayList<>()) // 빈 채팅 리스트
                .projectUsers(List.of(ProjectUser.builder()
                        .id(2L)
                        .role(ProjectRole.OWNER)
                        .project(project)
                        .user(user)
                        .createdAt(createAt)
                        .build())) // 빈 프로젝트 사용자 리스트
                .alarms(new ArrayList<>()) // 빈 알림 리스트
                .build();

        projectUser = ProjectUser.builder()
                .id(1L)
                .role(ProjectRole.OWNER)
                .project(project)
                .user(user)
                .createdAt(createAt)
                .build();
    }

    @Test
    void createFile_Success() {
        // Given

        FileDto.CreationReq creationReq = FileDto.CreationReq.builder()
                .name("dummy name")
                .extension("dummy ext")
                .path("dummy path")
                .parentId(1L)
                .build();

        File file = File.builder()
                .id(1L)
                .name("dummy name")
                .extension("dummy ext")
                .path("dummy path")
                .project(project)
                .parentFile(null)
                .childFiles(new ArrayList<>())
                .createdAt(createAt)
                .updatedAt(updateAt)
                .build();

        given(userService.getAuthenticatedUser(any())).willReturn(user);
        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(projectRepository.findById(any())).willReturn(Optional.of(project));
        given(fileRepository.save(any(File.class))).willReturn(File.of(creationReq, project, null));
        given(fileRepository.findById(any())).willReturn(Optional.of(file));

        // When
        Mockito.when(fileRepository.save(any()))
                .thenReturn(file);

        // Then
        FileDto.FileInfoRes fileInfoRes = fileService.createFile(1L, creationReq, token);

        Assertions.assertEquals(fileInfoRes.getId(), 1L);
        Assertions.assertEquals(fileInfoRes.getName(), "dummy name");
        Assertions.assertEquals(fileInfoRes.getExtension(), "dummy ext");
        Assertions.assertEquals(fileInfoRes.getProjectId(), null);
        Assertions.assertEquals(fileInfoRes.getParentId(), null);
        Assertions.assertEquals(fileInfoRes.getCreatedAt(), createAt);
        Assertions.assertEquals(fileInfoRes.getUpdatedAt(), updateAt);
    }

    @Test
    void createFile_UserNotFound() {
        // Given
        FileDto.CreationReq creationReq = FileDto.CreationReq.builder()
                .name("dummy name")
                .extension("dummy ext")
                .path("dummy path")
                .parentId(1L)
                .build();
        given(userService.getAuthenticatedUser(any())).willThrow(new UserNotFoundException(1L));

        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            fileService.createFile(project.getId(), creationReq, token);
        });
    }

    @Test
    void deleteFile_Success() {
        // Given
        Long fileId = 1L;
        File file = File.builder().id(fileId).name("dummyFile").extension("txt").build();
        given(userService.getAuthenticatedUser(any())).willReturn(user);
        given(projectRepository.findById(project.getId())).willReturn(Optional.of(project));
        given(fileRepository.findById(fileId)).willReturn(Optional.of(file));

        // When
        fileService.deleteFile(project.getId(), fileId, token);

        // Then
        verify(fileRepository).delete(file);
    }

    @Test
    void deleteFile_FileNotFound() {
        // Given
        Long fileId = 1L;
        given(userService.getAuthenticatedUser(any())).willReturn(user);
        given(projectRepository.findById(project.getId())).willReturn(Optional.of(project));
        given(fileRepository.findById(fileId)).willReturn(Optional.empty());

        // When & Then
        assertThrows(FileNotFoundException.class, () -> {
            fileService.deleteFile(project.getId(), fileId, token);
        });
    }

    @Test
    void getFileTree_Success() {
        // Given
        given(userService.getAuthenticatedUser(any())).willReturn(user);
        given(projectRepository.findById(project.getId())).willReturn(Optional.of(project));
        given(fileRepository.findByProjectIdAndParentFileIsNull(project.getId())).willReturn(List.of());

        // When
        FileDto.FileTreeListRes response = fileService.getFileTree(project.getId(), token);

        // Then
        assertNotNull(response);
        assertTrue(response.getFiles().isEmpty());
    }

    @Test
    void getFileContent_Success() throws IOException {
        // Given
        Long fileId = 1L;
        File file = File.builder().id(fileId).name("dummyFile").extension("txt").path("/dummy/path").build();
        given(userService.getAuthenticatedUser(any())).willReturn(user);
        given(projectRepository.findById(project.getId())).willReturn(Optional.of(project));
        given(fileRepository.findById(fileId)).willReturn(Optional.of(file));

        // Simulate file existence
        java.io.File existingFile = new java.io.File("/dummy/path/dummyFile.txt");
        existingFile.createNewFile();

        // When
        InputStreamResource resource = fileService.getFileContent(project.getId(), fileId, token);

        // Then
        assertNotNull(resource);
        verify(fileRepository).findById(fileId);
    }

    @Test
    void updateFile_Success() throws IOException {
        // Given
        Long fileId = 1L;
        File file = File.builder().id(fileId).name("dummyFile").extension("txt").build();
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        given(multipartFile.getBytes()).willReturn("new content".getBytes());
        given(userService.getAuthenticatedUser(any())).willReturn(user);
        given(projectRepository.findById(project.getId())).willReturn(Optional.of(project));
        given(fileRepository.findById(fileId)).willReturn(Optional.of(file));

        // When
        FileDto.FileInfoRes response = fileService.updateFile(project.getId(), fileId, multipartFile, token);

        // Then
        assertNotNull(response);
        verify(fileRepository).save(any(File.class));
    }

    @Test
    void updateFile_FileNotFound() {
        // Given
        Long fileId = 1L;
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        given(userService.getAuthenticatedUser(any())).willReturn(user);
        given(projectRepository.findById(project.getId())).willReturn(Optional.of(project));
        given(fileRepository.findById(fileId)).willReturn(Optional.empty());

        // When & Then
        assertThrows(FileNotFoundException.class, () -> {
            fileService.updateFile(project.getId(), fileId, multipartFile, token);
        });
    }
    */
}