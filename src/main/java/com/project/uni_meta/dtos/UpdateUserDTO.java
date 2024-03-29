package com.project.uni_meta.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data //toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO {
    @JsonProperty("username")
    @NotBlank(message = "User name is required")
    private String userName;

    @NotBlank(message = "Password is required")
    private String password;


    @JsonProperty("role_id")
    private Long roleId;

    @JsonProperty("faculty_id")
    private Long facultyId;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("user_active")
    private Boolean userActive;
}
