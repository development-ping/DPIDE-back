package com.dpide.dpide.controller;

import com.dpide.dpide.dto.ProjectDto;
import com.dpide.dpide.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/projects")
@RestController
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectDto.ProjectInfoRes> createProject(@RequestBody ProjectDto.CreationReq req,
                                                                   @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(projectService.createProject(req, token));
    }

    @GetMapping
    public ResponseEntity<ProjectDto.ProjectListRes> getProjects(@RequestHeader("Authorization") String token) {
        List<ProjectDto.ProjectInfoRes> projects = projectService.getProjects(token);
        return ResponseEntity.ok(new ProjectDto.ProjectListRes(projects));
    }

}
