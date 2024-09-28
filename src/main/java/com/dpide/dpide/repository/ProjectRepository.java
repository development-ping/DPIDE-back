package com.dpide.dpide.repository;

import com.dpide.dpide.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    // 특정 유저의 모든 프로젝트 목록 조회 (프로젝트 목록 read 기능 시 호출)
    List<Project> findByUserId(Long projectId);
}
