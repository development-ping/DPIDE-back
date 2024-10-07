package com.dpide.dpide.controller;

import com.dpide.dpide.dto.AlarmDto;
import com.dpide.dpide.service.AlarmService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.BDDMockito.given;
class AlarmControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    AlarmService alarmService;

    @Test
    @DisplayName("POST /alarm success")
    void makeInviteAlarm_Success() throws Exception {
        // Given
        String token = "Bearer dummyToken";
        AlarmDto.InviteReq inviteReq = AlarmDto.InviteReq.builder()
                .email("dummyEmail")
                .projectId(2L)
                .build();

        // When & Then
        mockMvc.perform(post("/alarm")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inviteReq)))
                .andExpect(status().isOk());

        // verify
        verify(alarmService, times(1)).makeInviteAlarm(any(AlarmDto.InviteReq.class), eq(token));
    }

    @Test
    void getAlarms() {
    }

    @Test
    void denyInvite() {
    }

    @Test
    void acceptInvite() {
    }
}