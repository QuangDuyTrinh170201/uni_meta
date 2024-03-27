package com.project.uni_meta.repositories;

import com.project.uni_meta.models.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    @Query("SELECT a FROM Article a " +
            "WHERE (:keyword IS NULL OR :keyword = '' OR a.name LIKE %:keyword%)")
    Page<Article> searchArticles(@Param("keyword") String keyword, Pageable pageable);
}
