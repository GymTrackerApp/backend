package com.gymtracker.app.dto.request;

import lombok.Builder;

@Builder
public record SignUp(String username, String email, String password) {
}
