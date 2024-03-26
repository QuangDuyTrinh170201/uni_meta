package com.project.uni_meta.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data //toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleImageDTO {
    @JsonProperty("article_id")
    @Min(value = 1, message = "Article id must >= 1")
    private Long articleId;

    @JsonProperty("image_url")
    @Size(min = 5, max = 200, message = "Image's name")
    private String imageUrl;
}
