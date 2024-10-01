package com.dpide.dpide.service;

import com.dpide.dpide.domain.Project;
import com.dpide.dpide.domain.ProjectRole;
import com.dpide.dpide.dto.ProjectDto;
import com.dpide.dpide.exception.UserNotFoundException;
import com.dpide.dpide.repository.ProjectRepository;
import com.dpide.dpide.user.config.TokenProvider;
import com.dpide.dpide.user.domain.User;
import com.dpide.dpide.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProjectService {
    private final TokenProvider tokenProvider;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectDto.ProjectInfoRes createProject(ProjectDto.CreationReq req, String token) {
        log.info("Creating a new project with name: {}", req.getName());

//        Long userId = tokenProvider.getUserId(token);
        Long userId = 1L;
        User user = validateUser(userId);

        // 프로젝트 생성 및 유저(오너) 추가
        Project project = Project.of(req, user);
        project.addUser(user, ProjectRole.OWNER);

        // 프로젝트와 유저 저장
        Project savedProject = projectRepository.save(project);

        log.info("Project created successfully: {}", savedProject.getId());
        return ProjectDto.ProjectInfoRes.of(savedProject);
    }

    public List<ProjectDto.ProjectInfoRes> getProjects(String token) {
//        Long userId = tokenProvider.getUserId(token);
        Long userId = 1L;
        validateUser(userId);

        return projectRepository.findByUserId(userId).stream()
                .map(ProjectDto.ProjectInfoRes::of)
                .toList();
    }

    private User validateUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
