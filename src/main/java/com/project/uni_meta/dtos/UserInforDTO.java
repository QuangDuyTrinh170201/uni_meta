package com.project.uni_meta.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data //toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInforDTO {

    @NotBlank(message = "password is required")
    private String password;

    @JsonProperty("retype_password")
    @NotBlank(message = "retype password is required")
    private String retypePassword;
}
