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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProjectService {
    private final UserService userService;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectUserRepository projectUserRepository;

    // controller <-> service <- repository(interface, DIP)
    // 의존성은 한방향으로 하는게 좋다..

    @Transactional
    public ProjectDto.ProjectInfoRes createProject(ProjectDto.CreationReq req, String token) {
        log.info("Creating a new project with name: {}", req.getName());

        Long userId = userService.getAuthenticatedUser(token).getId();
        User user = validateUser(userId);

        // 프로젝트 생성 및 유저(오너) 추가
        Project project = Project.of(req, user);
        project.addUser(user, ProjectRole.OWNER);

        Project savedProject = projectRepository.save(project);

        log.info("Project created successfully: {}", savedProject.getId());
        return ProjectDto.ProjectInfoRes.of(savedProject);
    }

    @Transactional
    public List<ProjectDto.ProjectInfoRes> getProjects(String token) {
        log.info("Getting projects");
        Long userId = userService.getAuthenticatedUser(token).getId();
        validateUser(userId);

        return projectRepository.findByUserId(userId).stream()
                .map(ProjectDto.ProjectInfoRes::of)
                .toList();
    }

    @Transactional
    public List<ProjectDto.ProjectInfoRes> getInvitedProjects(String token) {
        log.info("Getting invited projects");
        Long userId = userService.getAuthenticatedUser(token).getId();
        validateUser(userId);

        // 초대받은 프로젝트 목록
        return projectUserRepository.findByUserIdAndRole(userId, ProjectRole.PARTICIPANT).stream()
                .map(projectUser -> ProjectDto.ProjectInfoRes.of(projectUser.getProject()))
                .toList();
    }

    @Transactional
    public ProjectDto.ProjectInfoRes updateProject(Long projectId, ProjectDto.UpdateReq req, String token) {
        log.info("Updating project with id: {}", projectId);

        Long userId = userService.getAuthenticatedUser(token).getId();
        User user = validateUser(userId);
        Project project = validateProjectOwnership(projectId, user);

        // 프로젝트 정보 수정
        project.setName(req.getName());
        project.setDescription(req.getDescription());
        project.setUpdatedAt(LocalDateTime.now());

        Project updatedProject = projectRepository.save(project);

        log.info("Project updated successfully: {}", updatedProject.getId());

        return ProjectDto.ProjectInfoRes.of(updatedProject);
    }

    @Transactional
    public void deleteProject(Long projectId, String token) {
        log.info("Deleting project with id: {}", projectId);

        Long userId = userService.getAuthenticatedUser(token).getId();
        User user = validateUser(userId);

        Project project = validateProjectOwnership(projectId, user);

        // 프로젝트 삭제
        projectRepository.delete(project);
        log.info("Project deleted successfully: {}", projectId);
    }

    private User validateUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private Project validateProjectOwnership(Long projectId, User user) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        // projectUsers 초기화 (LAZY 로딩)
        Hibernate.initialize(project.getProjectUsers());

        // 프로젝트 소유자 확인
        project.getProjectUsers().stream()
                .filter(pu -> pu.getUser().equals(user) && pu.getRole() == ProjectRole.OWNER)
                .findFirst()
                .orElseThrow(() -> new ProjectOwnershipException(projectId, user.getId()));

        return project;
    }

    @Transactional
    public void leaveProject(Long projectId, String token) {
        User user = userService.getAuthenticatedUser(token);

        // 프로젝트 정보 가져오기
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        // 해당 유저가 속한 프로젝트 정보 가져오기
        ProjectUser projectUser = projectUserRepository.findByProjectAndUserAndRole(project, user, ProjectRole.PARTICIPANT)
                .orElseThrow(() -> new ProjectNotFoundException(project.getId()));

        // 삭제
        projectUserRepository.delete(projectUser);
        log.info("User successfully left from the project");
    }
}
