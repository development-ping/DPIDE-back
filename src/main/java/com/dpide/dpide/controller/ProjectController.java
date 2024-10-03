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
        log.info("CALL: ProjectController.createProject");
        return ResponseEntity.ok(projectService.createProject(req, token));
    }

    @GetMapping
    public ResponseEntity<ProjectDto.ProjectListRes> getProjects(@RequestHeader("Authorization") String token) {
        log.info("CALL: ProjectController.getProjects");
        List<ProjectDto.ProjectInfoRes> projects = projectService.getProjects(token);
        return ResponseEntity.ok(new ProjectDto.ProjectListRes(projects));
    }

    @GetMapping("/invited")
    public ResponseEntity<ProjectDto.ProjectListRes> getInvitedProjects(@RequestHeader("Authorization") String token) {
        log.info("CALL: ProjectController.getInvitedProjects");
        List<ProjectDto.ProjectInfoRes> projects = projectService.getInvitedProjects(token);
        return ResponseEntity.ok(new ProjectDto.ProjectListRes(projects));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectDto.ProjectInfoRes> updateProject(@PathVariable Long projectId,
                                                                   @RequestBody ProjectDto.UpdateReq req,
                                                                   @RequestHeader("Authorization") String token) {
        log.info("CALL: ProjectController.updateProject");
        return ResponseEntity.ok(projectService.updateProject(projectId, req, token));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId,
                                              @RequestHeader("Authorization") String token) {
        log.info("CALL: ProjectController.deleteProject");
        projectService.deleteProject(projectId, token);
        return ResponseEntity.noContent().build();
    }

}
