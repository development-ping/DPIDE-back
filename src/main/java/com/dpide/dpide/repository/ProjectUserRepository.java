package com.dpide.dpide.repository;

import com.dpide.dpide.domain.ProjectUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, Long> {
    // 특정 유저의 모든 공유받은 프로젝트 목록 조회 (공유받은 프로젝트 목록 read 기능 시 호출)
    List<ProjectUser> findByUserId(Long projectId);

    // 특정 projectId와 userId로 레코드 삭제 (프로젝트 나가기 기능 시 호출)
    // 서비스 계층에서 role 관련 제약조건을 추가해야 한다.
    void deleteByProjectIdAndUserId(Long projectId, Long userId);
}

