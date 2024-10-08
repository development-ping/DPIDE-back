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
import com.dpide.dpide.util.FileUtility;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

import com.dpide.dpide.exception.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

class FileServiceTest {

    private FileService fileService;
    private FileRepository fileRepository = Mockito.mock(FileRepository.class);
    private UserService userService = Mockito.mock(UserService.class);
    private ProjectRepository projectRepository = Mockito.mock(ProjectRepository.class);
    private UserRepository userRepository = Mockito.mock(UserRepository.class);

    private User user;
    private Project project;
    private ProjectUser projectUser;
    private String token = "Bearer 123";
    private LocalDateTime at = LocalDateTime.now();

    @BeforeEach
    public void setUp() {
        fileService = new FileService(userService, fileRepository, userRepository, projectRepository);

        user = User.builder()
                .id(1L)
                .email("dummy email")
                .nickname("dummy nickname")
                .password("dummy password")
                .createdAt(at)
                .updatedAt(at)
                .build();

        project = Project.builder()
                .id(1L)
                .name("dummy name")
                .description("dummy desc")
                .language("dummy language")
                .createdAt(at)
                .updatedAt(at)
                .user(user)
                .files(new ArrayList<>()) // 빈 파일 리스트
                .chats(new ArrayList<>()) // 빈 채팅 리스트
                .projectUsers(List.of(ProjectUser.builder()
                        .id(2L)
                        .role(ProjectRole.OWNER)
                        .project(project)
                        .user(user)
                        .createdAt(at)
                        .build())) // 빈 프로젝트 사용자 리스트
                .alarms(new ArrayList<>()) // 빈 알림 리스트
                .build();

        projectUser = ProjectUser.builder()
                .id(1L)
                .role(ProjectRole.OWNER)
                .project(project)
                .user(user)
                .createdAt(at)
                .build();

        String path = "/tmp/user_files/1/1/path/testFile.txt";
    }

    @Test
    void createFile_Success() throws IOException {
        // delete prev
        String path = "/tmp/user_files/1/1/path/testFile.txt";

        java.io.File createdFile = new java.io.File(path);
        if (createdFile.exists())
            createdFile.delete();

        // Given
        FileDto.CreationReq creationReq = FileDto.CreationReq.builder()
                .name("testFile")
                .extension("txt")
                .path("/path")
                .parentId(1L)
                .build();

        File file = File.builder()
                .id(1L)
                .name("testFile")
                .extension("txt")
                .path("/path")
                .project(project)
                .parentFile(null)
                .childFiles(new ArrayList<>())
                .createdAt(at)
                .updatedAt(at)
                .build();

        given(userService.getAuthenticatedUser(any())).willReturn(user);
        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(projectRepository.findById(any())).willReturn(Optional.of(project));
        given(fileRepository.save(any(File.class))).willReturn(File.of(creationReq, project, null));
        given(fileRepository.findById(any())).willReturn(Optional.of(file));

        java.io.File mockFile = mock(java.io.File.class);
        given(mockFile.exists()).willReturn(false); // File does not exist
        given(mockFile.createNewFile()).willReturn(true); // Successfully create new file

        // When
        Mockito.when(fileRepository.save(any()))
                .thenReturn(file);

        // Then
        FileDto.FileInfoRes fileInfoRes = fileService.createFile(1L, creationReq, token);

        Assertions.assertEquals(fileInfoRes.getId(), 1L);
        Assertions.assertEquals(fileInfoRes.getName(), "testFile");
        Assertions.assertEquals(fileInfoRes.getExtension(), "txt");
        Assertions.assertEquals(fileInfoRes.getProjectId(), 1L);
        Assertions.assertEquals(fileInfoRes.getParentId(), -1);
        Assertions.assertEquals(fileInfoRes.getCreatedAt(), at);
        Assertions.assertEquals(fileInfoRes.getUpdatedAt(), at);

        // delete
        createdFile = new java.io.File(path);
        if (createdFile.exists())
            createdFile.delete();
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
        given(userRepository.findById(any())).willReturn(Optional.of(user));

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
        given(userRepository.findById(any())).willReturn(Optional.of(user));

        Mockito.when(fileRepository.findById(any()))
                .thenThrow(new FileNotFoundException(fileId));

        // When & Then
        assertThrows(FileNotFoundException.class, () -> {
            fileService.deleteFile(project.getId(), fileId, token);
        });
    }

    @Test
    void getFileTree_Success() {
        // Given
        Long projectId = 1L;

        // Mocking user and project validation
        given(userService.getAuthenticatedUser(any())).willReturn(user);
        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));

        // Mocking file repository response
        List<File> mockFiles = new ArrayList<>();
        File parentFile = File.builder()
                .id(1L)
                .name("Parent Folder")
                .extension("")
                .path("/path/parent")
                .project(project)
                .parentFile(null)
                .childFiles(new ArrayList<>())
                .createdAt(at)
                .updatedAt(at)
                .build();

        File childFile = File.builder()
                .id(2L)
                .name("Child File")
                .extension("txt")
                .path("/path/parent/child")
                .project(project)
                .parentFile(parentFile)
                .childFiles(new ArrayList<>())
                .createdAt(at)
                .updatedAt(at)
                .build();

        mockFiles.add(parentFile);
        mockFiles.add(childFile);

        // Setting up the repository to return the mock files
        given(fileRepository.findByProjectIdAndParentFileIsNull(projectId)).willReturn(mockFiles);

        // When
        FileDto.FileTreeListRes fileTreeListRes = fileService.getFileTree(projectId, token);

        // Then
        Assertions.assertNotNull(fileTreeListRes);
        Assertions.assertNotNull(fileTreeListRes.getFiles());
        Assertions.assertEquals(2, fileTreeListRes.getFiles().size());

        // Validate parent file
        FileDto.FileTreeRes parentFileDto = fileTreeListRes.getFiles().get(0);
        Assertions.assertEquals("Parent Folder", parentFileDto.getName());
        Assertions.assertEquals("", parentFileDto.getExtension());
        Assertions.assertEquals("/path/parent", parentFileDto.getPath());

        // Validate child file
        FileDto.FileTreeRes childFileDto = fileTreeListRes.getFiles().get(1);
        Assertions.assertEquals("Child File", childFileDto.getName());
        Assertions.assertEquals("txt", childFileDto.getExtension());
        Assertions.assertEquals("/path/parent/child", childFileDto.getPath());
    }

    @Test
    void getFileContent_Success() throws IOException {
        // Given
        Long projectId = 1L;
        Long fileId = 1L;
        String path = "/tmp/user_files/1/1/path/testFile.txt";

        // Creating a mock file to be returned by the repository
        File file = File.builder()
                .id(fileId)
                .name("testFile")
                .extension("txt")
                .path("/path")
                .project(project)
                .parentFile(null)
                .childFiles(new ArrayList<>())
                .createdAt(at)
                .updatedAt(at)
                .build();

        // Mocking user and project validation
        given(userService.getAuthenticatedUser(any())).willReturn(user);
        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));
        given(fileRepository.findById(any())).willReturn(Optional.of(file));

        Path filePath = Paths.get(path);
        Files.createDirectories(filePath.getParent());

        // 파일 생성
        Path createdFile;

        if (!Files.exists(filePath))
            Files.createFile(filePath);

        java.io.File mockFile = mock(java.io.File.class);

        given(mockFile.createNewFile()).willReturn(true);
        given(mockFile.exists()).willReturn(true); // File does not exist

        // When
        InputStreamResource inputStreamResource = fileService.getFileContent(projectId, fileId, token);

        // Then
        Assertions.assertNotNull(inputStreamResource);
        Assertions.assertNotNull(inputStreamResource.getInputStream());
    }

    @Test
    void updateFile_Success() throws IOException {
        // delete prev
        String path = "/tmp/user_files/1/1/path/testFile.pdf";

        java.io.File existingFile = new java.io.File(path);

        if (existingFile.exists())
            existingFile.delete();

        // Create a file to update
        Files.createDirectories(existingFile.getParentFile().toPath());
        Files.createFile(existingFile.toPath());

        // Given
        FileDto.CreationReq creationReq = FileDto.CreationReq.builder()
                .name("testFile")
                .extension("pdf")
                .path("/path")
                .parentId(1L)
                .build();

        File existingFileEntity = File.builder()
                .id(1L)
                .name("testFile")
                .extension("pdf")
                .path("/path")
                .project(project)
                .parentFile(null)
                .childFiles(new ArrayList<>())
                .createdAt(at)
                .updatedAt(at)
                .build();

        // Mocking user and project validation
        given(userService.getAuthenticatedUser(any())).willReturn(user);
        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(projectRepository.findById(any())).willReturn(Optional.of(project));
        given(fileRepository.findById(any())).willReturn(Optional.of(existingFileEntity));

        // Mocking the MultipartFile
        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        given(mockMultipartFile.getBytes()).willReturn("Updated file content".getBytes());

        // When
        FileDto.FileInfoRes fileInfoRes = fileService.updateFile(1L, 1L, mockMultipartFile, token);

        // Then
        Assertions.assertEquals(fileInfoRes.getId(), existingFileEntity.getId());
        Assertions.assertEquals(fileInfoRes.getName(), "testFile");
        Assertions.assertEquals(fileInfoRes.getExtension(), "pdf");
        Assertions.assertEquals(fileInfoRes.getProjectId(), project.getId());
        Assertions.assertEquals(fileInfoRes.getParentId(), -1);
        Assertions.assertEquals(fileInfoRes.getCreatedAt(), at);
        Assertions.assertEquals(fileInfoRes.getUpdatedAt(), at);

        // Verify the content of the updated file
        String updatedContent = new String(Files.readAllBytes(existingFile.toPath()));
        Assertions.assertEquals("Updated file content", updatedContent);

        // Clean up
        if (existingFile.exists())
            existingFile.delete();
    }

    @Test
    void updateFile_FileNotFound() {
        // Given
        Long fileId = 1L;
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        given(userService.getAuthenticatedUser(any())).willReturn(user);
        given(projectRepository.findById(project.getId())).willReturn(Optional.of(project));
        given(fileRepository.findById(fileId)).willReturn(Optional.empty());
        given(userRepository.findById(any())).willReturn(Optional.of(user));

        // When & Then
        assertThrows(FileNotFoundException.class, () -> {
            fileService.updateFile(project.getId(), fileId, multipartFile, token);
        });
    }
}