package com.gymtracker.app.service;

import com.gymtracker.app.dto.request.SignIn;
import com.gymtracker.app.dto.request.SignUp;
import com.gymtracker.app.dto.response.SignInResponse;
import com.gymtracker.app.entity.User;
import com.gymtracker.app.exception.UserAlreadyExistsException;
import com.gymtracker.app.exception.SignInException;
import com.gymtracker.app.mapper.UserMapper;
import com.gymtracker.app.repository.UserRepository;
import com.gymtracker.app.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Clock clock;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, UserMapper userMapper, Clock clock, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.clock = clock;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public void signUp(SignUp signUp) {
        if (userRepository.existsByEmail(signUp.email()))
            throw new UserAlreadyExistsException("A user with this email already exists.");

        User user = userMapper.signUpToUser(signUp);
        user.setCreatedAt(Instant.now(clock));
        user.setPasswordHash(passwordEncoder.encode(signUp.password()));

        userRepository.save(user);
    }

    @Override
    public SignInResponse signIn(SignIn signIn) {
        User user = userRepository.findByEmail(signIn.email())
                .orElseThrow(() -> new SignInException("Email or password incorrect"));

        if (!passwordEncoder.matches(signIn.password(), user.getPasswordHash()))
            throw new SignInException("Email or password incorrect");

        String jwt = jwtService.generateToken(user.getUsername(), user.getUserId().toString());

        return new SignInResponse(user.getUsername(), jwt);
    }
}
