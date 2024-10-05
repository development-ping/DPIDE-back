package com.dpide.dpide.websocket.service;

import com.dpide.dpide.exception.ProjectNotFoundException;
import com.dpide.dpide.exception.UserNotFoundException;
import com.dpide.dpide.websocket.domain.Chat;
import com.dpide.dpide.websocket.dto.ChatDto;
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
import java.util.stream.Collectors;

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
    public ChatDto.ChatListRes getChatHistory(Long projectId) {
        // 0번 페이지에서 50개의 채팅 메시지만 가져옴
        Pageable pageable = PageRequest.of(0, 50);
        Page<Chat> chatPage = chatRepository.findByProjectId(projectId, pageable);

        // Chat 엔티티를 ChatInfo DTO로 변환
        List<ChatDto.ChatInfo> chatInfoList = chatPage.getContent().stream()
                .map(chat -> ChatDto.ChatInfo.builder()
                        .senderName(chat.getUser().getNickname())  // 보낸 사람 이름
                        .content(chat.getContent())                  // 채팅 내용
                        .createdAt(chat.getCreatedAt())              // 채팅 생성 시간
                        .build()
                )
                .collect(Collectors.toList());

        // ChatListRes로 변환하여 반환
        return ChatDto.ChatListRes.builder()
                .chatInfoList(chatInfoList)
                .build();
    }


    @Transactional(readOnly = true)
    public ChatDto.ChatListRes searchMessagesByKeyword(ChatDto.ChatSearch chatSearch) {
        // 특정 프로젝트에서 키워드를 포함하는 채팅 메시지를 검색
        List<Chat> messages = chatRepository.findByProjectIdAndContentContaining(chatSearch.getProjectId(), chatSearch.getKeyword());

        // Chat 엔티티를 ChatInfo DTO로 변환
        List<ChatDto.ChatInfo> chatInfoList = messages.stream()
                .map(chat -> ChatDto.ChatInfo.builder()
                        .senderName(chat.getUser().getNickname())  // 보낸 사람 이름
                        .content(chat.getContent())                  // 채팅 내용
                        .createdAt(chat.getCreatedAt())              // 채팅 생성 시간
                        .build()
                )
                .collect(Collectors.toList());

        // ChatListRes로 변환하여 반환
        return ChatDto.ChatListRes.builder()
                .chatInfoList(chatInfoList)
                .build();
    }

}
