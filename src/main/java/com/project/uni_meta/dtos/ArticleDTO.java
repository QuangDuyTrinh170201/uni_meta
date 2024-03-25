package com.project.uni_meta.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Data //toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDTO {
    @NotBlank(message = "Article name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @JsonProperty("filename")
    private String fileName;

    @JsonProperty("submission_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submissionDate;

    @NotBlank(message = "Status is required")
    private String status;

    @NotBlank(message = "View article is required")
    private Long view;

    @JsonProperty("academic_id")
    @NotBlank(message = "Academic is required")
    private Long academicId;

    @JsonProperty("user_id")
    @NotBlank(message = "User submit is required")
    private Long userId;

    @JsonProperty("faculty_id")
    @NotBlank(message = "Faculty is required")
    private Long facultyId;
}
