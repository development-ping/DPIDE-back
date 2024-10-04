package com.dpide.dpide.repository;

import com.dpide.dpide.domain.Alarm;
import com.dpide.dpide.domain.Project;
import com.dpide.dpide.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findAllByRecipient(User recipient);

    boolean existsByRecipientAndProject(User recipient, Project project);
}
