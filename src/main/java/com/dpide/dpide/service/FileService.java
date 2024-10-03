package com.dpide.dpide.service;

import com.dpide.dpide.domain.File;
import com.dpide.dpide.domain.Project;
import com.dpide.dpide.domain.ProjectRole;
import com.dpide.dpide.domain.ProjectUser;
import com.dpide.dpide.dto.FileDto;
import com.dpide.dpide.exception.FileNotFoundException;
import com.dpide.dpide.exception.ProjectNotFoundException;
import com.dpide.dpide.exception.ProjectOwnershipException;
import com.dpide.dpide.exception.UserNotFoundException;
import com.dpide.dpide.repository.FileRepository;
import com.dpide.dpide.repository.ProjectRepository;
import com.dpide.dpide.repository.ProjectUserRepository;
import com.dpide.dpide.user.domain.User;
import com.dpide.dpide.user.repository.UserRepository;
import com.dpide.dpide.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileService {
    private final UserService userService;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;

    public FileDto.FileInfoRes createFile(Long projectId, FileDto.CreationReq req, String token) {
        log.info("Creating a new file with name: {}", req.getName());

        Long userId = userService.getAuthenticatedUser(token).getId();

        validateUser(userId);
        Project project = validateProject(projectId);
        validateOwnership(projectId, userId);
        File parentFile = validateParentFile(req.getParentId());

        // TODO: 파일 생성 로직 추가

        File file = fileRepository.save(File.of(req, project, parentFile));
        return FileDto.FileInfoRes.of(file);
    }

    public void deleteFile(Long projectId, Long fileId, String token) {
        log.info("Deleting a file with id: {}", fileId);

        Long userId = userService.getAuthenticatedUser(token).getId();

        validateUser(userId);
        validateProject(projectId);
        validateOwnership(projectId, userId);

        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));

        fileRepository.delete(file);
    }

    public InputStreamResource getFileContent(Long projectId, Long fileId, String token) throws IOException {
        log.info("Getting a file with id: {}", fileId);

        Long userId = userService.getAuthenticatedUser(token).getId();

        validateUser(userId);
        validateProject(projectId);
        validateOwnership(projectId, userId);

        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));

        // TODO: file path 바꾸기..~
        String path = file.getPath() + file.getName() + "." + file.getExtension();
        java.io.File fileToRead = new java.io.File(path);
        return new InputStreamResource(new FileInputStream(fileToRead));
    }

    public FileDto.FileInfoRes updateFile(Long projectId, Long fileId, String name, MultipartFile content, String token) {
        log.info("Updating a file with id: {}", fileId);

        Long userId = userService.getAuthenticatedUser(token).getId();

        validateUser(userId);
        validateProject(projectId);
        validateOwnership(projectId, userId);

        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));

        String path = file.getPath() + file.getName() + "." + file.getExtension();

        // TODO: name 변경 로직 추가, content 변경 로직 추가

        return FileDto.FileInfoRes.of(file);
    }

    private Project validateProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));
    }

    private User validateUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private void validateOwnership(Long projectId, Long userId) {
        ProjectUser projectUser = projectUserRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ProjectOwnershipException(projectId, userId));

        if (projectUser.getRole() != ProjectRole.OWNER) {
            throw new ProjectOwnershipException(projectId, userId);
        }
    }

    private File validateParentFile(Long parentId) {
        if (parentId != -1) {
            return fileRepository.findById(parentId)
                    .orElseThrow(() -> new FileNotFoundException(parentId));
        }
        return null;
    }
}
