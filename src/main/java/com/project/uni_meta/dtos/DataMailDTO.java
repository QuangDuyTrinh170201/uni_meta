package com.project.uni_meta.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataMailDTO {
    private String to;
    private String subject;
    private String content;
    private Map<String, Object> props;
}
