package com.gymtracker.app.integration.auth;

import com.gymtracker.app.TestController;
import com.gymtracker.app.config.UtilsConfig;
import com.gymtracker.app.domain.User;
import com.gymtracker.app.security.JwtAuthenticationFilter;
import com.gymtracker.app.security.JwtService;
import com.gymtracker.app.security.SecurityConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

@WebMvcTest(controllers = TestController.class)
@Import({JwtAuthenticationFilter.class, JwtService.class, UtilsConfig.class, SecurityConfig.class})
class ProtectedEndpointTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(webTestClient);
    }

    @Test
    void givenNoAuthorizationToken_whenGetProtectedEndpoint_thenReturnsUnauthorized() {
        webTestClient.get()
                .uri("/protected")
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30", // without Bearer
    })
    void givenAuthorizationToken_whenInvalidToken_thenReturnsUnauthorized(String token) {
        webTestClient.get()
                .uri("/protected")
                .header("Authorization", token)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void givenValidAuthorizationToken_whenGetProtectedEndpoint_thenReturnsOkStatus() {
        UUID userId = UUID.randomUUID();

        Mockito.when(userDetailsService.loadUserByUsername(Mockito.anyString())).thenReturn(User.builder()
                .userId(userId)
                .build());

        webTestClient.get()
                .uri("/protected")
                .header("Authorization", "Bearer " + jwtService.generateToken("test", userId.toString()))
                .exchange()
                .expectStatus()
                .isOk();
    }
}
