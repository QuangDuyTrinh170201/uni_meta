package com.project.uni_meta.repositories;

import com.project.uni_meta.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
