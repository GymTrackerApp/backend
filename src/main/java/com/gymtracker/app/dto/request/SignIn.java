package com.gymtracker.app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SignIn(
        @NotBlank(message = "Email is required")
        @Email(message = "Email address should follow the pattern: user@domain.com")
        String email,

        @NotBlank(message = "Password is required")
        String password) {
}
