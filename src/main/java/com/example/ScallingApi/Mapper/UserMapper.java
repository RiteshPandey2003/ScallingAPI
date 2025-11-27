package com.example.ScallingApi.Mapper;

import com.example.ScallingApi.Entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    private final ModelMapper mapper = new ModelMapper();

    public User convertToDto(User entity) {
        return entity; // no DTO, return same object
    }
}
