package com.gymtracker.app.integration;

import com.gymtracker.app.dto.request.SignIn;
import com.gymtracker.app.dto.request.SignUp;
import com.gymtracker.app.entity.UserEntity;
import com.gymtracker.app.repository.jpa.SpringDataJpaUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserAuthTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18.1-alpine");

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private SpringDataJpaUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DynamicPropertySource
    static void setTestDatabaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(webTestClient);
    }

    @Test
    void givenValidUserDetails_whenRegisteringUser_thenSuccess() {
        SignUp signUp = SignUp.builder()
                .username("testuser")
                .email("test.user@domain.com")
                .password("testuser123")
                .build();

        webTestClient.post().uri("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signUp)
                .exchange()
                .expectStatus()
                .isCreated();

        UserEntity user = userRepository.findAll().iterator().next();

        Assertions.assertNotNull(user);
        Assertions.assertEquals(signUp.username(), user.getUsername());
        Assertions.assertNotEquals(signUp.password(), user.getPasswordHash());
    }

    @Test
    void givenUserAlreadyCreated_whenRegisteringUserWithSameEmail_thenThrowsException() {
        SignUp signUp = SignUp.builder()
                .username("testuser")
                .email("test.user@domain.com")
                .password("testuser123")
                .build();

        UserEntity user = UserEntity.builder()
                .username("testuser")
                .email("test.user@domain.com")
                .passwordHash(passwordEncoder.encode("testuser123"))
                .createdAt(Instant.now())
                .build();

        userRepository.save(user);

        webTestClient.post().uri("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signUp)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void givenValidUserDetails_whenLoggingUser_thenReturnsToken() {
        String plainPassword = "testuser123";
        String hashedPassword = passwordEncoder.encode(plainPassword);

        SignIn signIn = SignIn.builder()
                .email("test.user@domain.com")
                .password(plainPassword)
                .build();

        UserEntity user = UserEntity.builder()
                .email("test.user@domain.com")
                .createdAt(Instant.now())
                .username("testuser")
                .passwordHash(hashedPassword)
                .build();

        userRepository.save(user);

        webTestClient.post().uri("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signIn)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.token")
                .exists();
    }
}
