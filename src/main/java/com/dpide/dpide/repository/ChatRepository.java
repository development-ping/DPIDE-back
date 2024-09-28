package com.dpide.dpide.repository;

import com.dpide.dpide.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    // 특정 프로젝트의 채팅 메세지 목록 조회 (파일 목록 read 기능 시 호출)
    List<Chat> findByProjectId(Long projectId);

    // 특정 프로젝트에서 키워드를 포함하는 채팅 메시지를 조회 (채팅 search 기능 시 호출)
    List<Chat> findByProjectIdAndContentContaining(Long projectId, String keyword);
}
