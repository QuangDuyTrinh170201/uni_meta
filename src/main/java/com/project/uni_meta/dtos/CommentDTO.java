package com.project.uni_meta.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data //toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    @NotBlank(message = "Content is required")
    private String content;

    @JsonProperty("user_id")
    @NotBlank(message = "User id is required")
    private Long userId;

    @JsonProperty("article_id")
    @NotBlank(message = "Article id is required")
    private Long articleId;
}
