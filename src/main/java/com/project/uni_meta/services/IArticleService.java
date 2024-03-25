package com.project.uni_meta.services;

import com.project.uni_meta.dtos.ArticleDTO;
import com.project.uni_meta.exceptions.DataNotFoundException;
import com.project.uni_meta.models.Article;

import java.util.List;

public interface IArticleService {
    List<Article> getAllArticles();

    Article addArticle(ArticleDTO articleDTO) throws Exception;

    Article updateArticle(Long id, ArticleDTO articleDTO) throws Exception;

    void deleteArticle(Long id) throws Exception;

    public Article updateArticleFile(Long articleId, String fileName) throws DataNotFoundException;
}
