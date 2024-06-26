package com.project.uni_meta.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.uni_meta.models.Article;
import com.project.uni_meta.models.Comment;
import com.project.uni_meta.models.Image;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleResponse extends BaseResponse{
    private Long id;
    private String name;
    private String description;
    private String fileName;
    private LocalDateTime submissionDate;
    private String status;
    private Long view;
    private Boolean publish;


    @JsonProperty("faculty_name")
    private String facultyName;

    @JsonProperty("faculty_id")
    private Long facultyId;

    @JsonProperty("academic_year")
    private String academicYearName;

    @JsonProperty("academic_year_id")
    private Long academicYearId;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("product_images")
    private List<Image> articleImage = new ArrayList<>();

    @JsonProperty("article_comment")
    private List<Comment> articleComment = new ArrayList<>();

    public static ArticleResponse fromArticle(Article article) {
        ArticleResponse articleResponse = null;
        articleResponse = ArticleResponse.builder()
                .id(article.getId())
                .name(article.getName())
                .description(article.getDescription())
                .submissionDate(article.getSubmissionDate())
                .status(article.getStatus())
                .publish(article.getPublish())
                .view(article.getView())
                .facultyId(article.getFaculty().getId())
                .facultyName(article.getFaculty().getName())
                .academicYearId(article.getAcademicYear().getId())
                .academicYearName(article.getAcademicYear().getYear())
                .userId(article.getUser().getId())
                .userName(article.getUser().getUsername())
                .fileName(article.getFileName())
                .articleImage(article.getArticleImage())
                .articleComment(article.getArticleComment())
                .build();
        articleResponse.setCreatedAt(article.getCreatedAt());
        articleResponse.setUpdatedAt(article.getUpdatedAt());
        return articleResponse;
    }
}
