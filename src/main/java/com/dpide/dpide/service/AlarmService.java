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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AlarmService {
    private final AlarmRepository alarmRepository;
    private final UserService userService;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectUserRepository projectUserRepository;

    // 다른 사용자에게 알람 보내기
    @Transactional
    public void makeInviteAlarm(AlarmDto.InviteReq req , String token) {
        // 초대한 사람
        User sender = userService.getAuthenticatedUser(token);

        // 초대된 프로젝트
        Project project = projectRepository.findById(req.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(req.getProjectId()));

        // 초대한 사람이 소유권자인지 확인
        ProjectUser projectOwner = projectUserRepository.findByProjectAndUserAndRole(project, sender, ProjectRole.OWNER)
                .orElseThrow(() -> new ProjectNotFoundException(project.getId()));

        // 초대받은 유저 검색
        User invitedUser = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new EmailNotFoundException(req.getEmail()));

        // 이미 초대된 유저인지 확인
        if (projectUserRepository.existsByProjectAndUser(project, invitedUser)) {
            throw new UserAlreadyParticipantException();
        }

        // 이미 보낸 알림이 있는 지 확인
        if (alarmRepository.existsByRecipientAndProject(invitedUser, project)) {
            throw new DuplicateAlarmException();
        }

        Alarm notification = Alarm.builder()
                .sender(sender)
                .recipient(invitedUser)
                .project(project)
                .build();

        log.info("User successfully invited: {}", req.getEmail());
        alarmRepository.save(notification);
    }

    // 본인한테 온 모든 알람 조회
    @Transactional
    public AlarmDto.AlarmRes getAlarms(String token) {
        // 인증된 사용자 정보 가져오기
        User recipient = userService.getAuthenticatedUser(token);

        // 해당 사용자의 알람을 모두 조회하고 AlarmInfo로 변환
        List<AlarmDto.AlarmInfo> alarmInfoList = alarmRepository.findAllByRecipient(recipient).stream()
                .map(alarm -> AlarmDto.AlarmInfo.builder()
                        .id(alarm.getId())                         // 알람 ID
                        .senderName(alarm.getSender().getUsername()) // 보낸 사람 이름
                        .projectName(alarm.getProject().getName())   // 프로젝트 이름
                        .isRead(alarm.isRead())                      // 읽음 여부
                        .build()
                )
                .collect(Collectors.toList());

        log.info("Alarms successfully returned");
        // AlarmRes 객체에 AlarmInfo 리스트를 담아 반환
        return AlarmDto.AlarmRes.builder()
                .alarmInfoList(alarmInfoList)
                .build();
    }

    // 읽음 표시를 true로 바꾼다.
    @Transactional
    public void readAlarm(Long alarmId) {
        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(InvalidAlarmIdException::new);

        alarm.setRead(true);
        log.info("Marked as read.");
        alarmRepository.save(alarm);
    }

    @Transactional
    public void acceptAlarm(Long alarmId) {
        // 알람 ID로 알람을 조회하고, 없으면 예외 처리
        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(InvalidAlarmIdException::new);

        // 알람에서 사용자와 프로젝트 정보 추출
        User recipient = alarm.getRecipient();   // 알림을 받은 사용자
        Project project = alarm.getProject();    // 초대받은 프로젝트

        //이미 초대된 사용자인지 체크
        if (projectUserRepository.existsByProjectAndUser(project, recipient)) {
            throw new UserAlreadyParticipantException();
        }

        // ProjectUser 생성 및 저장
        ProjectUser projectUser = ProjectUser.builder()
                .user(recipient)
                .project(project)
                .role(ProjectRole.PARTICIPANT)   // 역할은 PARTICIPANT로 설정
                .build();

        projectUserRepository.save(projectUser);

        // 알람을 읽음 처리
        readAlarm(alarmId);

        log.info("User with ID {} accepted the alarm and joined project ID {}", recipient.getId(), project.getId());
    }

}
