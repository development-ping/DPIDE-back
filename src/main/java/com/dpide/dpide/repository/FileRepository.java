package com.dpide.dpide.repository;

import com.dpide.dpide.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    // 특정 프로젝트의 모든 파일 목록 조회 (파일 목록 read 기능 시 호출)
    List<File> findByProjectId(Long projectId);
}
