package com.project.uni_meta.repositories;

import com.project.uni_meta.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    Optional<User> findByUsername (String username);

    List<User> findByFacultyId(Long facultyId);

    List<User> findAll();

    Optional<User> findByAvatar(String avatar);

    boolean existsByEmail(String email);
}
