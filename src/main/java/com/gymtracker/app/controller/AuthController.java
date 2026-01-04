package com.gymtracker.app.controller;

import com.gymtracker.app.dto.request.RefreshTokenRequest;
import com.gymtracker.app.dto.request.SignIn;
import com.gymtracker.app.dto.request.SignUp;
import com.gymtracker.app.dto.response.MessageResponse;
import com.gymtracker.app.dto.response.RefreshTokenResponse;
import com.gymtracker.app.dto.response.SignInResponse;
import com.gymtracker.app.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<MessageResponse> signUp(@Valid @RequestBody SignUp signUp) {
        authService.signUp(signUp);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("User registered successfully"));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponse> signIn(@Valid @RequestBody SignIn signIn) {
        SignInResponse response = authService.signIn(signIn);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        RefreshTokenResponse refreshTokenResponse = authService
                .refreshToken(refreshTokenRequest.refreshToken());

        return ResponseEntity.status(HttpStatus.OK)
                .body(refreshTokenResponse);
    }
}
