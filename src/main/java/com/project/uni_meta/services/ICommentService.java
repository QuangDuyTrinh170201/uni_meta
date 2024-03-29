package com.project.uni_meta.services;

import com.project.uni_meta.dtos.CommentDTO;
import com.project.uni_meta.models.Comment;

import java.util.List;

public interface ICommentService {
    List<Comment> getAllComments();
    Comment addComment(CommentDTO commentDTO);
    Comment updateComment(Long id, CommentDTO commentDTO);
    void deleteComment(Long id);
}