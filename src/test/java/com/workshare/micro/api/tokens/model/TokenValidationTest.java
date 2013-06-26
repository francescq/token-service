package com.workshare.micro.api.tokens.model;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;

import com.workshare.micro.utils.UUIDGenerator;

public class TokenValidationTest {

    private Token token;

    private UUIDGenerator uuids;
    private Validator validator;
    private Date futureDate;
    private Date date;
    private String createUser;
    private Set<ConstraintViolation<Token>> constraintViolations;

    @Before
    public void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        uuids = new UUIDGenerator();
        futureDate = new Date();
        futureDate.setTime(futureDate.getTime() + 5000000);
        date = new Date();
        createUser = "createUser";
    }

    private String getStringLength(int i) {
        StringBuilder s = new StringBuilder();
        for (int j = 0; j < i; j++) {
            s.append(i);
        }
        return s.toString();
    }

    @Test
    public void shouldValidateNotNullToken() throws Exception {

        token = new Token(uuids.generateString(), null, null, futureDate, date, null);

        constraintViolations = validator.validate(token);

        // 3 notnull + 2 notEmpty
        assertEquals(5, constraintViolations.size());
    }

    @Test
    public void shouldValidateStringLengthToken() throws Exception {
        token = new Token(uuids.generateString(), getStringLength(21), getStringLength(2001), futureDate, date, createUser);

        constraintViolations = validator.validate(token);

        // content overflow, type overflow
        assertEquals(2, constraintViolations.size());
    }

    @Test
    public void shouldValidateTypeContentNotEmpty() throws Exception {
        token = new Token(uuids.generateString(), "", "", futureDate, date, createUser);

        constraintViolations = validator.validate(token);

        assertEquals(2, constraintViolations.size());
    }

    @Test
    public void shouldExpiryDateNotBeAPastDate() throws Exception {
        Date expiry = new Date();
        expiry.setTime(1);
        token = new Token(uuids.generateString(), "aaa", "aaa", date, date, createUser);

        constraintViolations = validator.validate(token);

        assertEquals(1, constraintViolations.size());
    }
}
