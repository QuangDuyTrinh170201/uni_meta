package com.project.uni_meta.responses;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleListResponse {
    private List<ArticleResponse> articles;
    private int totalPages;
}
