package com.dpide.dpide.service;

import com.dpide.dpide.repository.ProjectRepository;
import com.dpide.dpide.repository.ProjectUserRepository;
import com.dpide.dpide.user.repository.UserRepository;
import com.dpide.dpide.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class ProjectServiceTest {
    ProjectService projectService;
    private final UserService userService = Mockito.mock(UserService.class);
    private final ProjectRepository projectRepository = Mockito.mock(ProjectRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final ProjectUserRepository projectUserRepository = Mockito.mock(ProjectUserRepository.class);
}