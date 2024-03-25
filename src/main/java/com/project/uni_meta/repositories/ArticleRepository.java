package com.project.uni_meta.repositories;

import com.project.uni_meta.models.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {

}
