package com.gymtracker.app.service.impl;

import com.gymtracker.app.domain.User;
import com.gymtracker.app.dto.request.SignIn;
import com.gymtracker.app.dto.request.SignUp;
import com.gymtracker.app.dto.response.SignInResponse;
import com.gymtracker.app.exception.SignInException;
import com.gymtracker.app.exception.UserAlreadyExistsException;
import com.gymtracker.app.mapper.UserMapper;
import com.gymtracker.app.repository.UserRepository;
import com.gymtracker.app.security.JwtService;
import com.gymtracker.app.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public void signUp(SignUp signUp) {
        if (userRepository.existsByEmail(signUp.email()) || userRepository.existsByUsername(signUp.username()))
            throw new UserAlreadyExistsException("A user with this email or username already exists.");

        User user = userMapper.signUpToUser(signUp);
        user.updatePassword(passwordEncoder.encode(signUp.password()));

        userRepository.save(user);
    }

    @Override
    public SignInResponse signIn(SignIn signIn) {
        User user = userRepository.findByEmail(signIn.email())
                .orElseThrow(() -> new SignInException("Email or password incorrect"));

        if (!passwordEncoder.matches(signIn.password(), user.getPassword()))
            throw new SignInException("Email or password incorrect");

        String jwt = jwtService.generateToken(user.getDisplayUsername(), user.getUsername());

        return new SignInResponse(user.getDisplayUsername(), jwt);
    }
}
