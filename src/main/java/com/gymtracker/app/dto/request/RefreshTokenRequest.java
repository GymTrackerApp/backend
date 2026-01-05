package com.gymtracker.app.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record RefreshTokenRequest(
        @NotEmpty(message = "Refresh token must not be empty")
        String refreshToken) {
}
