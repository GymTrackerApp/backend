package com.gymtracker.app.dto.request;

import lombok.Builder;

@Builder
public record SignIn(String email, String password) {
}
