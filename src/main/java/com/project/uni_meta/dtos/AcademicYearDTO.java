package com.project.uni_meta.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data //toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AcademicYearDTO {
    @NotBlank(message = "Year is required")
    private String year;

    @NotBlank(message = "Current year is required")
    private Long current;
}
