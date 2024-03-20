package com.project.uni_meta.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data //toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @JsonProperty("username")
    @NotBlank(message = "User name is required")
    private String userName;

    @NotBlank(message = "Password is required")
    private String password;

    @JsonProperty("retype_password")
    @NotBlank(message = "Retype password is required")
    private String retypePassword;

    @NotNull(message = "Role id is required")
    @JsonProperty("role_id")
    private Long roleId;

//    @NotNull(message = "Faculty id is required")
    @JsonProperty("faculty_id")
    private Long facultyId;
}
