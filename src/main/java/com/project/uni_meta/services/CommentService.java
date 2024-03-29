package com.project.uni_meta.services;

import com.project.uni_meta.dtos.CommentDTO;
import com.project.uni_meta.exceptions.DataNotFoundException;
import com.project.uni_meta.models.Article;
import com.project.uni_meta.models.Comment;
import com.project.uni_meta.models.User;
import com.project.uni_meta.repositories.ArticleRepository;
import com.project.uni_meta.repositories.CommentRepository;
import com.project.uni_meta.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService{
    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Override
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @Override
    public Comment addComment(CommentDTO commentDTO) {
        Article article = null;
        try {
            article = articleRepository.findById(commentDTO.getArticleId())
                    .orElseThrow(() -> new DataNotFoundException("Article not found with id: " + commentDTO.getArticleId()));
        } catch (DataNotFoundException e) {
            throw new RuntimeException(e);
        }

        User user = null;
        try {
            user = userRepository.findById(commentDTO.getUserId())
                    .orElseThrow(() -> new DataNotFoundException("User not found with id: " + commentDTO.getUserId()));
        } catch (DataNotFoundException e) {
            throw new RuntimeException(e);
        }

        Comment comment = Comment.builder()
                .content(commentDTO.getContent())
                .article(article)
                .user(user)
                .build();

        return commentRepository.save(comment);
    }

    @Override
    public Comment updateComment(Long id, CommentDTO commentDTO) {
        Comment existingComment = null;
        try {
            existingComment = commentRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException("Comment not found with id: " + id));
        } catch (DataNotFoundException e) {
            throw new RuntimeException(e);
        }

        Article article = null;
        try {
            article = articleRepository.findById(commentDTO.getArticleId())
                    .orElseThrow(() -> new DataNotFoundException("Article not found with id: " + commentDTO.getArticleId()));
        } catch (DataNotFoundException e) {
            throw new RuntimeException(e);
        }

        User user = null;
        try {
            user = userRepository.findById(commentDTO.getUserId())
                    .orElseThrow(() -> new DataNotFoundException("User not found with id: " + commentDTO.getUserId()));
        } catch (DataNotFoundException e) {
            throw new RuntimeException(e);
        }

        existingComment.setContent(commentDTO.getContent());
        existingComment.setArticle(article);
        existingComment.setUser(user);

        return commentRepository.save(existingComment);
    }

    @Override
    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            try {
                throw new DataNotFoundException("Comment not found with id: " + id);
            } catch (DataNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        commentRepository.deleteById(id);
    }
}
