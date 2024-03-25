package com.project.uni_meta.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Data //toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClosureDTO {
    @NotBlank(message = "Deadline is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deadline;

    @JsonProperty("final_deadline")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @NotBlank(message = "Final deadline is required")
    private LocalDateTime finalDeadline;

    @JsonProperty("faculty_id")
    @NotBlank(message = "Faculty id is required")
    private Long facultyId;

    @JsonProperty("academic_year_id")
    @NotBlank(message = "Academic year id is required")
    private Long academicYearId;
}
