package com.gymtracker.app.mapper;

import com.gymtracker.app.dto.request.SignUp;
import com.gymtracker.app.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    User signUpToUser(SignUp signUp);
}
