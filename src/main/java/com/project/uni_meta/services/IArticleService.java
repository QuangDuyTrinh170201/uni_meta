package com.project.uni_meta.services;

import com.project.uni_meta.dtos.ArticleDTO;
import com.project.uni_meta.dtos.ArticleImageDTO;
import com.project.uni_meta.dtos.MailDTO;
import com.project.uni_meta.exceptions.DataNotFoundException;
import com.project.uni_meta.models.Article;
import com.project.uni_meta.models.Image;
import com.project.uni_meta.responses.ArticleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IArticleService {
    public Page<ArticleResponse> getAllArticles(String keyword, Long userId, Long facultyId, PageRequest pageRequest);

    Article addArticle(ArticleDTO articleDTO) throws Exception;

    Article updateArticle(Long id, ArticleDTO articleDTO) throws Exception;

    void deleteArticle(Long id) throws Exception;

    public Article updateArticleFile(Long articleId, String fileName) throws DataNotFoundException;
    public Image createArticleImage(Long articleId, ArticleImageDTO articleImageDTO) throws Exception;

    public Article getArticleById(long productId) throws Exception;

    boolean sendMail(MailDTO mailDTO);

    List<Image> getImagesByArticleId(Long articleId);

    public List<ArticleResponse> getArticlesByUserId(Long userId) throws DataNotFoundException;
    public List<ArticleResponse> getArticlesByAllParam(String keyword, Long userId, Long facultyId, Long academicYearId);
}
