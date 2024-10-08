package com.dpide.dpide.controller;

import com.dpide.dpide.dto.AlarmDto;
import com.dpide.dpide.service.AlarmService;
import com.dpide.dpide.user.config.WebSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.BDDMockito.given;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


@AutoConfigureMockMvc
@WebMvcTest(AlarmController.class)
class AlarmControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    AlarmService alarmService;

    @Test
    @WithMockUser
        // 인증된 사용자로 설정
    void makeInviteAlarm_Success() throws Exception {
        // Given
        String token = "Bearer 123";

        AlarmDto.InviteReq inviteReq = AlarmDto.InviteReq.builder()
                .email("dummy email")
                .projectId(2L)
                .build();

        // When & Then
        mockMvc.perform(post("/alarm")
                        .header("Authorization", token)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inviteReq)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
        // 인증된 사용자로 설정
    void getAlarms_Success() throws Exception {
        String token = "Bearer 123";

        mockMvc.perform(get("/alarm")
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
        // 인증된 사용자로 설정
    void denyInvite_Success() throws Exception {
        Long alarmId = 1L;
        String token = "Bearer 123";

        mockMvc.perform(put("/alarm/{alarmId}/deny", alarmId)
                        .header("Authorization", token)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
        // 인증된 사용자로 설정
    void acceptInvite_Success() throws Exception {
        Long alarmId = 1L;
        String token = "Bearer 123";

        mockMvc.perform(put("/alarm/{alarmId}/accept", alarmId)
                        .header("Authorization", token)
                        .with(csrf()))
                .andExpect(status().isOk());

    }
}