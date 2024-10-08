package com.dpide.dpide.repository;

import com.dpide.dpide.domain.Project;
import com.dpide.dpide.domain.ProjectRole;
import com.dpide.dpide.domain.ProjectUser;
import com.dpide.dpide.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, Long> {
    // 특정 유저의 모든 공유받은 프로젝트 목록 조회 (공유받은 프로젝트 목록 read 기능 시 호출)
    List<ProjectUser> findByUserId(Long projectId);

    // 특정 projectId와 userId로 레코드 삭제 (프로젝트 나가기 기능 시 호출)
    // 서비스 계층에서 role 관련 제약조건을 추가해야 한다.
    void deleteByProjectIdAndUserId(Long projectId, Long userId);

    // 특정 프로젝트와 유저의 연관관계 조회
    Optional<ProjectUser> findByProjectAndUserAndRole(Project project, User user, ProjectRole role);

    // 이미 초대된 유저인지 확인
    boolean existsByProjectAndUser(Project project, User user);
    // 특정 유저의 특정 역할을 가진 프로젝트 목록 조회
    List<ProjectUser> findByUserIdAndRole(Long userId, ProjectRole role);
}

