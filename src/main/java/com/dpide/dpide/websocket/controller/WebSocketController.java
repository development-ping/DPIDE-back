package com.dpide.dpide.websocket.controller;

import com.dpide.dpide.websocket.dto.ChatDto;
import com.dpide.dpide.websocket.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    // WebSocket을 통한 메시지 처리
    @MessageMapping("/message")
    public void receiveMessage(ChatDto.ChatMessage message) {
        // 메시지 저장
        chatService.saveMessage(message.getContent(), message.getProjectId(), message.getUserId());

        // 해당 채팅방 구독자들에게 메시지 전송
        messagingTemplate.convertAndSend("/topic/chatroom/" + message.getProjectId(), message);
    }

    @MessageMapping("/code")
    public void receiveCode(ChatDto.ChatMessage message) {

        // 해당 채팅방 구독자들에게 메시지 전송
        messagingTemplate.convertAndSend("/topic/project/" + message.getProjectId(), message);
    }

    @MessageMapping("/join")
    public void receiveJoinMessage(ChatDto.ChatMessage message) {

        // 해당 채팅방 구독자들에게 메시지 전송
        messagingTemplate.convertAndSend("/topic/join/" + message.getProjectId(), message);
    }

    //채팅 내역 조회
    @GetMapping("/chat/{projectId}")
    public ResponseEntity<ChatDto.ChatListRes> getChatHistory(@PathVariable Long projectId) {
        // 서비스에서 ChatListRes를 받아 바로 반환
        ChatDto.ChatListRes chatListRes = chatService.getChatHistory(projectId);
        return ResponseEntity.ok(chatListRes);
    }


    // 특정 프로젝트에서 키워드를 포함하는 메시지 검색
    @PostMapping("/chat/search")
    public ResponseEntity<ChatDto.ChatListRes> searchMessages(@RequestBody ChatDto.ChatSearch chatSearch) {
        // 서비스에서 ChatListRes를 받아 반환
        ChatDto.ChatListRes chatListRes = chatService.searchMessagesByKeyword(chatSearch);
        return ResponseEntity.ok(chatListRes);
    }

}
