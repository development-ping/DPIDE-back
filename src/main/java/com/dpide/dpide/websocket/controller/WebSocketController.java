package com.dpide.dpide.websocket.controller;

import com.dpide.dpide.websocket.domain.Chat;
import com.dpide.dpide.websocket.dto.ChatMessage;
import com.dpide.dpide.websocket.dto.ChatSearch;
import com.dpide.dpide.websocket.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    // WebSocket을 통한 메시지 처리
    @MessageMapping("/message")
    public void receiveMessage(ChatMessage message) {
        // 메시지 저장
        chatService.saveMessage(message.getContent(), message.getProjectId(), message.getUserId());

        // 해당 채팅방 구독자들에게 메시지 전송
        messagingTemplate.convertAndSend("/topic/chatroom/" + message.getProjectId(), message);
    }

    //채팅 내역 조회
    @GetMapping("/chat/{projectId}")
    public ResponseEntity<List<Chat>> getChatHistory(@PathVariable Long projectId) {
        List<Chat> chatHistory = chatService.getChatHistory(projectId);
        return ResponseEntity.ok(chatHistory);
    }

    // 특정 프로젝트에서 키워드를 포함하는 메시지 검색
    @GetMapping("/chat/search")
    public ResponseEntity<List<Chat>> searchMessages(@RequestBody ChatSearch chatSearch) {
        List<Chat> messages = chatService.searchMessagesByKeyword(chatSearch);
        return ResponseEntity.ok(messages);
    }
}
