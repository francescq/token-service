package com.workshare.micro.api.tokens.model;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;

public class TokenRequestValidationTest {

    private TokenRequest tokenRequest;

    private Validator validator;
    private Set<ConstraintViolation<TokenRequest>> constraintViolations;

    @Before
    public void setup() {
        tokenRequest = new TokenRequest();
        tokenRequest.type = "type";
        tokenRequest.content = "content";
        tokenRequest.maxAge = 1000;// miliseconds

        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void shouldValidateTokenRequestOk() throws Exception {
        constraintViolations = validator.validate(tokenRequest);

        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldValidateTokenRequestMaxAgePositive() throws Exception {
        tokenRequest.maxAge = 0;

        constraintViolations = validator.validate(tokenRequest);

        assertEquals(1, constraintViolations.size());
    }

    @Test
    public void shouldValidateTokenRequestTypeContentNotNull() throws Exception {
        tokenRequest.type = null;
        tokenRequest.content = null;

        constraintViolations = validator.validate(tokenRequest);

        // not null + not blank
        assertEquals(4, constraintViolations.size());
    }

    @Test
    public void shouldValidateTokenRequestTypeContentNotBlank() throws Exception {
        tokenRequest.type = "";
        tokenRequest.content = "";

        constraintViolations = validator.validate(tokenRequest);

        // not blank
        assertEquals(2, constraintViolations.size());
    }

}
