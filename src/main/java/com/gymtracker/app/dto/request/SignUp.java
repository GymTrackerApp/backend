package com.gymtracker.app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record SignUp(
        @NotBlank(message = "Username is required")
        @Size(min = 2, max = 25, message = "Username length should be 2-25 characters")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Email address should follow the pattern: user@domain.com.")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 128, message = "The password must be between 8-128 characters long")
        String password) {
}
