package com.gymtracker.app.service.impl;

import com.gymtracker.app.domain.User;
import com.gymtracker.app.dto.response.UserProfileResponse;
import com.gymtracker.app.exception.UserDoesNotExistException;
import com.gymtracker.app.mapper.UserMapper;
import com.gymtracker.app.repository.UserRepository;
import com.gymtracker.app.service.UserService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserProfileResponse getUserProfile(UUID userId) {
        User user = userRepository.findByIdWithoutCollections(userId)
                .orElseThrow(() -> new UserDoesNotExistException("Cannot get profile for non-existing user"));

        return userMapper.userToUserProfileResponse(user);
    }
}
