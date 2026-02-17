package com.gymtracker.app.service.impl;

import com.gymtracker.app.domain.User;
import com.gymtracker.app.dto.response.UserProfileResponse;
import com.gymtracker.app.exception.UserDoesNotExistException;
import com.gymtracker.app.mapper.UserMapper;
import com.gymtracker.app.repository.UserRepository;
import com.gymtracker.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final MessageSource messageSource;

    @Override
    public UserProfileResponse getUserProfile(UUID userId) {
        User user = userRepository.findByIdWithoutCollections(userId)
                .orElseThrow(() -> new UserDoesNotExistException(
                        messageSource.getMessage("user-does-not-exist-exception.getting-profile", null, LocaleContextHolder.getLocale())
                ));

        return userMapper.userToUserProfileResponse(user);
    }
}
