package com.project.uni_meta.configurations;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;

public class MapperConfigurations {
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}
