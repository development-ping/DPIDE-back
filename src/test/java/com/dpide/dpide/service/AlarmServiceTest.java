package com.dpide.dpide.service;

import com.dpide.dpide.domain.Alarm;
import com.dpide.dpide.domain.Project;
import com.dpide.dpide.domain.ProjectRole;
import com.dpide.dpide.domain.ProjectUser;
import com.dpide.dpide.dto.AlarmDto;
import com.dpide.dpide.exception.*;
import com.dpide.dpide.repository.AlarmRepository;
import com.dpide.dpide.repository.ProjectRepository;
import com.dpide.dpide.repository.ProjectUserRepository;
import com.dpide.dpide.user.domain.User;
import com.dpide.dpide.user.repository.UserRepository;
import com.dpide.dpide.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

class AlarmServiceTest {
    AlarmService alarmService;
    private AlarmRepository alarmRepository = Mockito.mock(AlarmRepository.class);
    private UserService userService = Mockito.mock(UserService.class);
    private ProjectRepository projectRepository = Mockito.mock(ProjectRepository.class);
    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private ProjectUserRepository projectUserRepository = Mockito.mock(ProjectUserRepository.class);

    private User sender;
    private User invitedUser;
    private Project project;
    @BeforeEach
    public void setUpTest(){
        alarmService = new AlarmService(alarmRepository, userService, projectRepository, userRepository, projectUserRepository);

        sender = User.builder()
                .id(1L)
                .email("dummy email 1")
                .nickname("dummy nickname 1")
                .password("dummy password 1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        invitedUser = User.builder()
                .id(2L)
                .email("dummy email 2")
                .nickname("dummy nickname 2")
                .password("dummy password 2")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User user = User.builder()
                .email("dummy email3")
                .password("dummy password 3")
                .nickname("dummy nickname 3")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 더미 Project 객체 생성
        project = Project.builder()
                .name("dummy name 4")
                .description("dummy desc 4")
                .language("dummy language")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(user)
                .files(new ArrayList<>()) // 빈 파일 리스트
                .chats(new ArrayList<>()) // 빈 채팅 리스트
                .projectUsers(new ArrayList<>()) // 빈 프로젝트 사용자 리스트
                .alarms(new ArrayList<>()) // 빈 알림 리스트
                .build();
    }
    @Test
    void makeInviteAlarm_Success() {
        // Given
        AlarmDto.InviteReq req = new AlarmDto.InviteReq(1L, "invited@example.com");

        given(userService.getAuthenticatedUser(anyString())).willReturn(sender);
        given(projectRepository.findById(req.getProjectId())).willReturn(Optional.of(project));
        given(projectUserRepository.findByProjectAndUserAndRole(project, sender, ProjectRole.OWNER))
                .willReturn(Optional.of(new ProjectUser()));
        given(userRepository.findByEmail(req.getEmail())).willReturn(Optional.of(invitedUser));
        given(projectUserRepository.existsByProjectAndUser(project, invitedUser)).willReturn(false);
        given(alarmRepository.existsByRecipientAndProject(invitedUser, project)).willReturn(false);

        // When
        alarmService.makeInviteAlarm(req, "token");

        // Then
        ArgumentCaptor<Alarm> alarmCaptor = ArgumentCaptor.forClass(Alarm.class);
        verify(alarmRepository).save(alarmCaptor.capture());
        assertEquals(sender, alarmCaptor.getValue().getSender());
        assertEquals(invitedUser, alarmCaptor.getValue().getRecipient());
        assertEquals(project, alarmCaptor.getValue().getProject());
    }

    @Test
    void makeInviteAlarm_UserAlreadyParticipant() {
        // Given
        AlarmDto.InviteReq req = new AlarmDto.InviteReq(1L, "invited@example.com");

        given(userService.getAuthenticatedUser(anyString())).willReturn(sender);
        given(projectRepository.findById(req.getProjectId())).willReturn(Optional.of(project));
        given(projectUserRepository.findByProjectAndUserAndRole(project, sender, ProjectRole.OWNER))
                .willReturn(Optional.of(new ProjectUser()));
        given(userRepository.findByEmail(req.getEmail())).willReturn(Optional.of(invitedUser));
        given(projectUserRepository.existsByProjectAndUser(project, invitedUser)).willReturn(true);

        // When & Then
        assertThrows(UserAlreadyParticipantException.class, () -> {
            alarmService.makeInviteAlarm(req, "token");
        });
    }

    @Test
    void makeInviteAlarm_DuplicateAlarm() {
        // Given
        AlarmDto.InviteReq req = new AlarmDto.InviteReq(1L, "invited@example.com");

        given(userService.getAuthenticatedUser(anyString())).willReturn(sender);
        given(projectRepository.findById(req.getProjectId())).willReturn(Optional.of(project));
        given(projectUserRepository.findByProjectAndUserAndRole(project, sender, ProjectRole.OWNER))
                .willReturn(Optional.of(new ProjectUser()));
        given(userRepository.findByEmail(req.getEmail())).willReturn(Optional.of(invitedUser));
        given(projectUserRepository.existsByProjectAndUser(project, invitedUser)).willReturn(false);
        given(alarmRepository.existsByRecipientAndProject(invitedUser, project)).willReturn(true);

        // When & Then
        assertThrows(DuplicateAlarmException.class, () -> {
            alarmService.makeInviteAlarm(req, "token");
        });
    }

    @Test
    void makeInviteAlarm_ProjectNotFound() {
        // Given
        AlarmDto.InviteReq req = new AlarmDto.InviteReq(1L, "invited@example.com");

        given(userService.getAuthenticatedUser(anyString())).willReturn(sender);
        // 프로젝트가 존재하지 않을 경우
        given(projectRepository.findById(req.getProjectId())).willReturn(Optional.empty());

        // When & Then
        assertThrows(ProjectNotFoundException.class, () -> {
            alarmService.makeInviteAlarm(req, "token");
        });
    }

    @Test
    void makeInviteAlarm_UserNotOwner() {
        // Given
        AlarmDto.InviteReq req = new AlarmDto.InviteReq(1L, "invited@example.com");

        given(userService.getAuthenticatedUser(anyString())).willReturn(sender);
        given(projectRepository.findById(req.getProjectId())).willReturn(Optional.of(project));
        // 초대자가 소유자가 아닐 경우
        given(projectUserRepository.findByProjectAndUserAndRole(project, sender, ProjectRole.OWNER))
                .willReturn(Optional.empty());

        // When & Then
        assertThrows(ProjectNotFoundException.class, () -> {
            alarmService.makeInviteAlarm(req, "token");
        });
    }

    @Test
    void makeInviteAlarm_EmailNotFound() {
        // Given
        AlarmDto.InviteReq req = new AlarmDto.InviteReq(1L, "invited@example.com");

        given(userService.getAuthenticatedUser(anyString())).willReturn(sender);
        given(projectRepository.findById(req.getProjectId())).willReturn(Optional.of(project));
        given(projectUserRepository.findByProjectAndUserAndRole(project, sender, ProjectRole.OWNER))
                .willReturn(Optional.of(new ProjectUser()));
        // 초대받은 유저가 존재하지 않을 경우
        given(userRepository.findByEmail(req.getEmail())).willReturn(Optional.empty());

        // When & Then
        assertThrows(EmailNotFoundException.class, () -> {
            alarmService.makeInviteAlarm(req, "token");
        });
    }

    @Test
    void getAlarms_Success() {
        // Given
        given(userService.getAuthenticatedUser(anyString())).willReturn(invitedUser);
        Alarm alarm1 = Alarm.builder().id(1L).sender(sender).project(project).isRead(false).build();
        Alarm alarm2 = Alarm.builder().id(2L).sender(sender).project(project).isRead(true).build();

        given(alarmRepository.findAllByRecipient(invitedUser)).willReturn(List.of(alarm1, alarm2));

        // When
        AlarmDto.AlarmRes response = alarmService.getAlarms("token");

        // Then
        assertEquals(2, response.getAlarmInfoList().size());
        assertEquals("dummy nickname 1", response.getAlarmInfoList().get(0).getSenderName());
        assertEquals("dummy name 4", response.getAlarmInfoList().get(0).getProjectName());
    }

    @Test
    void readAlarm_Success() {
        // Given
        Long alarmId = 1L;
        Alarm alarm = Alarm.builder().id(alarmId).isRead(false).build();
        given(alarmRepository.findById(alarmId)).willReturn(Optional.of(alarm));

        // When
        alarmService.readAlarm(alarmId);

        // Then
        assertTrue(alarm.isRead());
        verify(alarmRepository).save(alarm);
    }

    @Test
    void readAlarm_InvalidAlarmId() {
        // Given
        Long alarmId = 1L;
        given(alarmRepository.findById(alarmId)).willReturn(Optional.empty());

        // When & Then
        assertThrows(InvalidAlarmIdException.class, () -> {
            alarmService.readAlarm(alarmId);
        });
    }

    @Test
    void acceptAlarm_Success() {
        // Given
        Long alarmId = 1L;
        Alarm alarm = Alarm.builder().id(alarmId).recipient(invitedUser).project(project).build();
        given(alarmRepository.findById(alarmId)).willReturn(Optional.of(alarm));
        given(projectUserRepository.existsByProjectAndUser(project, invitedUser)).willReturn(false);

        // When
        alarmService.acceptAlarm(alarmId);

        // Then
        verify(projectUserRepository).save(any(ProjectUser.class));
        verify(alarmRepository).save(alarm);
        assertTrue(alarm.isRead());
    }

    @Test
    void acceptAlarm_UserAlreadyParticipant() {
        // Given
        Long alarmId = 1L;
        Alarm alarm = Alarm.builder().id(alarmId).recipient(invitedUser).project(project).build();
        given(alarmRepository.findById(alarmId)).willReturn(Optional.of(alarm));
        given(projectUserRepository.existsByProjectAndUser(project, invitedUser)).willReturn(true);

        // When & Then
        assertThrows(UserAlreadyParticipantException.class, () -> {
            alarmService.acceptAlarm(alarmId);
        });
    }

    @Test
    void acceptAlarm_InvalidAlarmId() {
        // Given
        Long alarmId = 1L;
        given(alarmRepository.findById(alarmId)).willReturn(Optional.empty());

        // When & Then
        assertThrows(InvalidAlarmIdException.class, () -> {
            alarmService.acceptAlarm(alarmId);
        });
    }
}