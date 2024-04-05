package com.project.uni_meta.repositories;

import com.project.uni_meta.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByArticleId(Long articleId);
}
