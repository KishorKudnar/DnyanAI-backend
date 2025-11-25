package com.dnyanai.DnyanAI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.dnyanai.DnyanAI.model.UserProgress;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    Optional<UserProgress> findByEmail(String email);
}
