package com.dpide.dpide.websocket.service;

import com.dpide.dpide.exception.ProjectNotFoundException;
import com.dpide.dpide.exception.UserNotFoundException;
import com.dpide.dpide.websocket.domain.Chat;
import com.dpide.dpide.websocket.dto.ChatSearch;
import com.dpide.dpide.websocket.repository.ChatRepository;
import com.dpide.dpide.domain.Project;
import com.dpide.dpide.user.domain.User;
import com.dpide.dpide.user.repository.UserRepository;
import com.dpide.dpide.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public void saveMessage(String content, Long projectId, Long userId) {
        // DB에서 User 객체 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // DB에서 Project 객체 조회
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        // 채팅 메시지 DB 저장
        Chat chat = Chat.builder()
                .content(content)
                .user(user)
                .project(project)
                .build();

        chatRepository.save(chat);
    }

    @Transactional(readOnly = true)
    public List<Chat> getChatHistory(Long projectId) {
        // 0번 페이지에서 50개의 채팅 메시지만 가져오도록 설정
        Pageable pageable = PageRequest.of(0, 50);
        Page<Chat> chatPage = chatRepository.findByProjectId(projectId, pageable);

        return chatPage.getContent();
    }

    @Transactional(readOnly = true)
    public List<Chat> searchMessagesByKeyword(ChatSearch chatSearch) {
        // 특정 프로젝트에서 키워드를 포함하는 채팅 메시지를 검색
        return chatRepository.findByProjectIdAndContentContaining(chatSearch.getProjectId(), chatSearch.getKeyword());
    }
}
