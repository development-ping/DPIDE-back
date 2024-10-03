package com.dpide.dpide.service;

import com.dpide.dpide.domain.File;
import com.dpide.dpide.domain.Project;
import com.dpide.dpide.domain.ProjectRole;
import com.dpide.dpide.dto.FileDto;
import com.dpide.dpide.exception.*;
import com.dpide.dpide.repository.FileRepository;
import com.dpide.dpide.repository.ProjectRepository;
import com.dpide.dpide.user.domain.User;
import com.dpide.dpide.user.repository.UserRepository;
import com.dpide.dpide.user.service.UserService;
import com.dpide.dpide.util.FileExecutor;
import com.dpide.dpide.util.FileUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.dpide.dpide.util.FileUtility.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileService {
    private final UserService userService;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final FileExecutor fileExecutor;

    @Transactional
    public FileDto.FileInfoRes createFile(Long projectId, FileDto.CreationReq req, String token) {
        log.info("Creating a new file with name: {}", req.getName());

        Long userId = userService.getAuthenticatedUser(token).getId();
        User user = validateUser(userId);

        validateUser(userId);
        Project project = validateProjectOwnership(projectId, user);
        File parentFile = validateParentFile(req.getParentId());

        // 부모 폴더에 동일한 이름의 파일 or 폴더가 있는지 확인
        validateFileNameUniqueness(projectId, req.getParentId(), req.getName());

        // 프로젝트 디렉터리 생성
        String projectPath = generateProjectPath(userId, projectId);
        createDirectory(projectPath);

        // 파일 or 폴더 생성
        if (isFile(req.getExtension())) {
            createNewFile(projectPath, req.getName(), req.getExtension());
        } else {
            createNewFolder(projectPath, req.getName());
        }

        File file = fileRepository.save(File.of(req, project, parentFile));
        return FileDto.FileInfoRes.of(file);
    }

    @Transactional
    public void deleteFile(Long projectId, Long fileId, String token) {
        log.info("Deleting a file with id: {}", fileId);

        Long userId = userService.getAuthenticatedUser(token).getId();
        User user = validateUser(userId);

        validateUser(userId);
        validateProjectOwnership(projectId, user);

        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));

        // 파일 or 폴더 경로 생성
        String path = generateFolderPath(userId, projectId, file.getName());
        if (isFile(file.getExtension())) {
            path += "." + file.getExtension();
        }

        // 파일 or 폴더 삭제
        FileUtility.deleteFileOrDirectory(path);
        fileRepository.delete(file);
    }

    public FileDto.FileTreeListRes getFileTree(Long projectId, String token) {
        Long userId = userService.getAuthenticatedUser(token).getId();
        User user = validateUser(userId);
        validateProjectOwnership(projectId, user);

        // 프로젝트에 속한 최상위 파일들만 가져오고, 자식 파일들까지 포함한 트리 구조 반환
        List<FileDto.FileTreeRes> files = fileRepository.findByProjectIdAndParentFileIsNull(projectId)
                .stream()
                .map(FileDto.FileTreeRes::of)
                .collect(Collectors.toList());

        return FileDto.FileTreeListRes.builder().files(files).build();
    }

    public InputStreamResource getFileContent(Long projectId, Long fileId, String token) throws IOException {
        log.info("Getting a file with id: {}", fileId);

        Long userId = userService.getAuthenticatedUser(token).getId();
        User user = validateUser(userId);

        validateUser(userId);
        validateProjectOwnership(projectId, user);

        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));

        // 실제 파일 경로 가져오기 (도커 컨테이너 내부 경로)
        String filePath = generateFilePath(userId, projectId, file.getName(), file.getExtension());
        java.io.File fileToRead = new java.io.File(filePath);

        // 파일이 존재하는지 확인
        if (!fileToRead.exists()) {
            throw new FileOperationException(file.getName());
        }

        return new InputStreamResource(new FileInputStream(fileToRead));
    }

    @Transactional
    public FileDto.FileInfoRes updateFile(Long projectId, Long fileId, MultipartFile content, String token) {
        log.info("Updating a file with id: {}", fileId);

        Long userId = userService.getAuthenticatedUser(token).getId();
        User user = validateUser(userId);

        validateUser(userId);
        validateProjectOwnership(projectId, user);

        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));

        // TODO: FileUtility로 파일 관련 메서드 빼기...
        // 실제 파일 경로 가져오기 (FileUtility 사용)
        String filePath = generateFilePath(userId, projectId, file.getName(), file.getExtension());
        java.io.File fileToUpdate = new java.io.File(filePath);

        // 파일 덮어쓰기
        try (FileOutputStream fos = new FileOutputStream(fileToUpdate)) {
            fos.write(content.getBytes());  // MultipartFile의 내용을 파일에 덮어쓰기
        } catch (IOException e) {
            throw new FileOperationException(file.getName());
        }

        return FileDto.FileInfoRes.of(file);
    }

    public String executeFile(Long projectId, Long fileId, String userInput, String token) {
        log.info("Executing file with id: {}", fileId);

        // 유저 인증
        Long userId = userService.getAuthenticatedUser(token).getId();
        validateUser(userId);

        // 파일 조회
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));

        // 파일 경로 생성
        String filePath = generateFilePath(userId, projectId, file.getName(), file.getExtension());

        //TODO: 파일 실행 결과 반환 (FileExecutor 사용)

        return "";
    }

    private User validateUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private Project validateProjectOwnership(Long projectId, User user) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        // 프로젝트 소유자 확인
        project.getProjectUsers().stream()
                .filter(pu -> pu.getUser().equals(user) && pu.getRole() == ProjectRole.OWNER)
                .findFirst()
                .orElseThrow(() -> new ProjectOwnershipException(projectId, user.getId()));

        return project;
    }

    private File validateParentFile(Long parentId) {
        if (parentId != -1) {
            return fileRepository.findById(parentId)
                    .orElseThrow(() -> new FileNotFoundException(parentId));
        }
        return null;
    }

    // 파일/폴더 이름 중복 확인 로직
    private void validateFileNameUniqueness(Long projectId, Long parentId, String fileName) {
        if (fileRepository.findByProjectIdAndParentFileIdAndName(projectId, parentId, fileName).isPresent()) {
            throw new DuplicateFileNameException(fileName);
        }
    }
}
