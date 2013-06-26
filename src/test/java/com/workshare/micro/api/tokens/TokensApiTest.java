package com.workshare.micro.api.tokens;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.workshare.micro.api.tokens.model.Token;
import com.workshare.micro.api.tokens.model.TokenRequest;
import com.workshare.micro.api.tokens.persistence.dao.TokensDao;
import com.workshare.micro.utils.UUIDGenerator;

@SuppressWarnings("unchecked")
public class TokensApiTest {

    private TokensApi api;
    private TokensDao dao;
    private UUIDGenerator uuids;

    private TokenRequest tokenRequest;

    private Validator validator;
    private ArgumentCaptor<Token> tokenCaptured;
    private Set<ConstraintViolation<Token>> constraintViolations;

    private static final String TOKEN_ID = "ID-XXX-ID";
    private static final String TOKEN_TYPE = "mytype";
    private static final String TOKEN_CONTENT = "{\"hello\":\"json\"}";
    private static final int TOKEN_MAXAGE = 2000;

    @Before
    public void setup() {
        dao = mock(TokensDao.class);

        uuids = mock(UUIDGenerator.class);
        when(uuids.generateString()).thenReturn(TOKEN_ID);

        api = new TokensApi(dao, uuids);

        tokenRequest = new TokenRequest();
        tokenRequest.content = TOKEN_CONTENT;
        tokenRequest.maxAge = TOKEN_MAXAGE;
        tokenRequest.type = TOKEN_TYPE;

        tokenCaptured = ArgumentCaptor.forClass(Token.class);

        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void shouldInvokeDaoOnCreateToken() throws Exception {

        api.createToken(tokenRequest);

        verify(dao).create(any(Token.class));
    }

    @Test
    public void shouldGetEmptyCollectionIfEmpty() throws Exception {
        when(dao.getTokens()).thenReturn(new ArrayList<Token>());

        Response res = api.getTokens();

        assertEquals(200, res.getStatus());
        assertTrue(((List<Token>) res.getEntity()).isEmpty());
    }

    private Date addMaxAge(int maxAge) {
        Date date = new Date();
        date.setTime(date.getTime() + maxAge);
        return date;
    }

    // @Test
    public void shouldExpiryDateAddMaxAgeToCurrentDate() throws Exception {
        Date expiryDate = addMaxAge(tokenRequest.maxAge);

        api.createToken(tokenRequest);

        verify(dao).create(tokenCaptured.capture());

        assertEquals(expiryDate, tokenCaptured.getValue().getExpiryDate());
    }

    // @Test
    public void shouldTokenHaveTypeAndContent() throws Exception {
        Date expiryDate = addMaxAge(tokenRequest.maxAge);
        api.createToken(tokenRequest);

        verify(dao).create(tokenCaptured.capture());

        assertEquals(expiryDate, tokenCaptured.getValue().getExpiryDate());
        assertEquals(tokenRequest.type, tokenCaptured.getValue().getType());
        assertEquals(tokenRequest.content, tokenCaptured.getValue().getContent());
    }

    @Test
    public void shouldGetCollection() throws Exception {
        Token expected = new Token(TOKEN_ID, "type", "content", new Date(), new Date(), "createUser");
        List<Token> list = Arrays.asList(expected, expected);
        when(dao.getTokens()).thenReturn(list);

        Response res = api.getTokens();

        assertEquals(200, res.getStatus());
        assertEquals(2, ((List<Token>) res.getEntity()).size());
    }

    @Test
    public void shouldReturnTokenUrlWhenDaoCreateSucceeds() throws Exception {

        Response res = api.createToken(tokenRequest);

        final Object currentLocation = res.getMetadata().get("Location").get(0).toString();
        final Object expectedLocation = TOKEN_ID;
        assertEquals(201, res.getStatus());
        assertEquals(expectedLocation, currentLocation);
    }

    @Test
    public void shouldInvokeDaoOnGetToken() throws Exception {
        api.getToken(TOKEN_ID);

        verify(dao).get(anyString());
    }

    @Test
    public void shouldReturnTokenWhenFound() throws Exception {
        Token expected = new Token(TOKEN_ID, "type", "content", new Date(), new Date(), "createUser");
        when(dao.get(TOKEN_ID)).thenReturn(expected);

        Response res = api.getToken(TOKEN_ID);

        assertEquals(expected, res.getEntity());
        assertEquals(200, res.getStatus());
    }

    @Test
    public void shouldReturn404WhenTokenFound() throws Exception {

        when(dao.get(TOKEN_ID)).thenReturn(null);

        Response res = api.getToken(TOKEN_ID);

        assertEquals(null, res.getEntity());
        assertEquals(404, res.getStatus());
    }

    @Test
    public void shouldInvokeDaoOnDeleteToken() throws Exception {

        api.deleteToken(TOKEN_ID);

        verify(dao).delete(eq(TOKEN_ID));
    }

    // @Test validation now occurs before tokenRequestArribes to api
    public void souldValidateApiCreateOk() throws Exception {
        Response res = api.createToken(tokenRequest);

        assertTrue(res.getStatus() == 201);
    }

    // @Test validation now occurs before tokenRequestArribes to api
    public void souldValidateApiCreateKo() throws Exception {
        tokenRequest.type = null;
        tokenRequest.content = null;
        tokenRequest.maxAge = 0;

        Response res = api.createToken(tokenRequest);

        assertTrue(res.getStatus() == 422);
    }
}
