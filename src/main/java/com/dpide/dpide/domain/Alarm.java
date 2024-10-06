package com.dpide.dpide.domain;

import com.dpide.dpide.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "alarms")
public class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 받는 사용자
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_id", nullable = false, foreignKey = @ForeignKey(name = "fk_notification_recipient"))
    private User recipient;

    // 보내는 사용자
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false, foreignKey = @ForeignKey(name = "fk_notification_sender"))
    private User sender;

    // 읽음 여부
    @Builder.Default
    @Column(nullable = false)
    private boolean isRead = false;

    // 초대받은 프로젝트 정보
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id", nullable = false, foreignKey = @ForeignKey(name = "fk_notification_project"))
    private Project project;

    // 알림 생성 시간
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
