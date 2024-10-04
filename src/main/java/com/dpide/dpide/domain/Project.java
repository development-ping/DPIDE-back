package com.dpide.dpide.domain;

import com.dpide.dpide.dto.ProjectDto;
import com.dpide.dpide.user.domain.User;
import com.dpide.dpide.websocket.domain.Chat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String language;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Project와 User의 다대일 관계 (생성한 사용자)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_project_user"))
    private User user;

    // Project와 File의 일대다 관계
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files;

    // Project와 Chat의 일대다 관계
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chat> chats;

    // Project와 User 간의 다대다 관계를 관리하는 Project_User 연결 테이블
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectUser> projectUsers;

    public static Project of(ProjectDto.CreationReq req, User user) {
        return Project.builder()
                .name(req.getName())
                .description(req.getDescription())
                .language(req.getLanguage())
                .user(user)
                .build();
    }

    // TODO: ProjectUser 추가 시 사용할 메소드
    public void addUser(User user, ProjectRole role) {
        if (this.projectUsers == null) {
            this.projectUsers = new ArrayList<>();
        }
        ProjectUser projectUser = ProjectUser.builder()
                .project(this)
                .user(user)
                .role(role)
                .build();
        projectUsers.add(projectUser);
    }
}


