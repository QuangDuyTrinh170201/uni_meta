package com.project.uni_meta.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailDTO {

    @JsonProperty("faculty_id")
    private Long facultyId;

    private String url;

    @JsonProperty("username")
    private String userName;

    private String password;
}
