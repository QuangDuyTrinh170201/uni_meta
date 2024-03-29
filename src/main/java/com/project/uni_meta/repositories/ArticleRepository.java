package com.project.uni_meta.repositories;

import com.project.uni_meta.models.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    @Query("SELECT a FROM Article a " +
            "WHERE (:keyword IS NULL OR :keyword = '' OR a.name LIKE %:keyword% OR a.description LIKE %:keyword%) " +
            "AND (:userId IS NULL OR :userId = 0 OR a.user.id = :userId) " +
            "AND (:facultyId IS NULL OR :facultyId = 0 OR a.faculty.id = :facultyId)")
    Page<Article> searchArticles(@Param("keyword") String keyword,
                                                               @Param("userId") Long userId,
                                                               @Param("facultyId") Long facultyId,
                                                               Pageable pageable);

    List<Article> findByUserId(Long userId);

}
