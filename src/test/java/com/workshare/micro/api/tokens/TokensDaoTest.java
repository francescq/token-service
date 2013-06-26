package com.workshare.micro.api.tokens;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;

import com.googlecode.flyway.core.Flyway;
import com.workshare.micro.api.tokens.model.Token;
import com.workshare.micro.api.tokens.persistence.dao.TokensDao;
import com.workshare.micro.utils.UUIDGenerator;

public class TokensDaoTest {

    private JdbcConnectionPool ds;
    private DBI dbi;
    private Handle h;
    private TokensDao dao;
    private Token token;
    private List<Token> tokenList;

    private UUIDGenerator uuids;
    private Flyway flyway;

    @Before
    public void connectDatabase() {
        ds = JdbcConnectionPool.create("jdbc:h2:mem:test", "username", "password");

        flyway = new Flyway();
        flyway.setDataSource(ds);
        flyway.migrate();

        dbi = new DBI(ds);
        h = dbi.open();
        dao = dbi.open(TokensDao.class);
        uuids = new UUIDGenerator();
        token = new Token(uuids.generateString(), "type", "content", new Date(), new Date(), "createUser");
    }

    @After
    public void closeDatabase() {
        flyway.clean();

        h.close();
        ds.dispose();
    }

    private void generateTokensList(int numTokens) {
        tokenList = new ArrayList<Token>();
        for (int i = 0; i < numTokens; i++) {
            Token t1 = new Token(i + "", i + "", i + "", new Date(), new Date(), i + "");
            tokenList.add(t1);
        }
    }

    @Test
    public void testCreateToken() {

        dao.create(token);

        Token tokenr = dao.get(token.getId());

        assertEquals(token, tokenr);
    }

    @Test
    public void testDeleteToken() {

        dao.create(token);

        dao.delete(token.getId());

        assertNull(dao.get(token.getId()));
    }

    @Test
    public void testCreateTokenGeneratesCreateDate() {
        dao.create(token);

        Token tokent = dao.get(token.getId());

        assertTrue(tokent.getCreateDate() instanceof Date);
        assertEquals(token.getType(), tokent.getType());
        assertEquals(token.getContent(), tokent.getContent());
        assertEquals(token.getExpiryDate(), tokent.getExpiryDate());
        assertEquals(token.getCreateUser(), tokent.getCreateUser());
        assertNotNull(tokent.getCreateDate());
    }

    @Test
    public void testCreateBean() {
        dao.create(token);

        Token tokenRes = dao.get(token.getId());
        assertNotNull(tokenRes);
    }

    @Test
    public void testGetToken() {
        dao.create(token);

        Token getToken = dao.get(token.getId());

        Assert.assertNotNull(getToken);
        Assert.assertEquals(token, getToken);
    }

    @Test
    public void testGetTokensCollection() {
        dao.create(token);
        Token token2 = new Token("2", "type", "content", new Date(), new Date(), "createUser");
        dao.create(token2);

        List<Token> list = dao.getTokens();

        Assert.assertEquals(2, list.size());
    }

    @Test
    public void testGetTokensEmptyCollection() {
        List<Token> list = dao.getTokens();

        assertTrue(list.isEmpty());
    }

    @Test
    public void testCreateTokensBatch() {

        generateTokensList(15);

        dao.createBatch(tokenList);

        List<Token> tokens = dao.getTokens();
        assertEquals(tokenList.size(), tokens.size());

    }

    @Test
    public void testGetNonExistentToken() {
        Token token = dao.get("nonexistent");

        Assert.assertNull(token);
    }

    @Test(expected = UnableToExecuteStatementException.class)
    public void testCreateDuplicateToken() {
        dao.create(token);
        dao.create(token);
    }

}