package com.dpide.dpide.repository;

import com.dpide.dpide.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    // 특정 프로젝트의 모든 파일 목록 조회 (파일 목록 read 기능 시 호출)
    List<File> findByProjectId(Long projectId);

    // 특정 프로젝트의 부모 파일이 같은 디렉토리 안에 동일한 이름의 파일이 있는지 확인
    Optional<File> findByProjectIdAndParentFileIdAndName(Long projectId, Long parentId, String name);

    // 프로젝트 내 최상위 파일들 조회 (parentFile이 없는 경우)
    List<File> findByProjectIdAndParentFileIsNull(Long projectId);
}
