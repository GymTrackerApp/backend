package com.gymtracker.app;

import com.gymtracker.app.dto.request.SignIn;
import com.gymtracker.app.dto.request.SignUp;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

class InputValidationTests {
    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @ParameterizedTest
    @CsvSource(value = {
            ",123",
            "'',123",
            "email,123",
            "email@,123"
    })
    void givenInvalidSignInEmail_whenValidated_ShouldViolateRules(String email, String password) {
        SignIn signIn = new SignIn(email, password);

        Set<ConstraintViolation<SignIn>> violationSet = validator.validate(signIn);

        Assertions.assertEquals(1, violationSet.size());
        Assertions.assertEquals("email", violationSet.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "email@domain.com,",
            "email@domain.com,''"
    })
    void givenInvalidSignInPassword_whenValidated_ShouldViolateRules(String email, String password) {
        SignIn signIn = new SignIn(email, password);

        Set<ConstraintViolation<SignIn>> violationSet = validator.validate(signIn);

        System.out.println(violationSet);

        Assertions.assertEquals(1, violationSet.size());
        Assertions.assertEquals("password", violationSet.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "email",
            "email@"
    })
    void givenInvalidSignUpEmail_whenValidated_ShouldViolateRules(String email) {
        SignUp signUp = new SignUp("username", email, "password");

        Set<ConstraintViolation<SignUp>> violationSet = validator.validate(signUp);

        Assertions.assertEquals(1, violationSet.size());
        Assertions.assertEquals("email", violationSet.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "123", // too short
            "012345678901234567890" // too long
    })
    void givenInvalidSignUpPassword_whenValidated_ShouldViolateRules(String password) {
        SignUp signUp = new SignUp("username", "email@domain.com", password);

        Set<ConstraintViolation<SignUp>> violationSet = validator.validate(signUp);

        System.out.println(violationSet);

        Assertions.assertFalse(violationSet.isEmpty());
        Assertions.assertEquals("password", violationSet.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "u", // too short
            "Lorem ipsum dolor sit amet" // too long
    })
    void givenInvalidSignUpUsername_whenValidated_ShouldViolateRules(String username) {
        SignUp signUp = new SignUp(username, "email@domain.com", "Password123@");

        Set<ConstraintViolation<SignUp>> violationSet = validator.validate(signUp);

        System.out.println(violationSet);

        Assertions.assertFalse(violationSet.isEmpty());
        Assertions.assertEquals("username", violationSet.iterator().next().getPropertyPath().toString());
    }
}
