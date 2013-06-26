package com.workshare.micro.api.tokens;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.workshare.micro.api.tokens.model.Token;
import com.workshare.micro.api.tokens.persistence.dao.TokensDao;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class CachingTokenDaoTest {

    private Token token;
    private TokensDao delegate;
    private CachingTokensDao dao;
    private ScheduledExecutorService scheduler;
    private Runnable scheduledTask;

    @Before
    public void before() {
        delegate = mock(TokensDao.class);
        scheduler = mock(ScheduledExecutorService.class);
        token = mock(Token.class);
        dao = new CachingTokensDao(delegate, scheduler);

        ArgumentCaptor<Runnable> runnableCaptured = ArgumentCaptor.forClass(Runnable.class);
        verify(scheduler).scheduleAtFixedRate(runnableCaptured.capture(), anyLong(), anyLong(), any(TimeUnit.class));
        scheduledTask = runnableCaptured.getValue();
    }

    @Test
    public void shouldInvokeCreateBatchOnDelegate() {
        Collection<Token> tokens = mock(Collection.class);
        dao.createBatch(tokens);
        verify(delegate).createBatch(tokens);
    }

    @Test
    public void shouldInvokeCloseOnDelegate() {
        dao.close();
        verify(delegate).close();
    }

    @Test
    public void shouldInvokeCreateOnDelegateAsyncronously() {

        dao.create(token);

        scheduledTask.run();

        ArgumentCaptor<Collection> batch = ArgumentCaptor.forClass(Collection.class);
        verify(delegate).createBatch(batch.capture());
        assertEquals(1, batch.getValue().size());
    }

    @Test
    public void shouldClearTheCacheAfterFlush() {

        dao.create(token);

        scheduledTask.run();
        scheduledTask.run();

        ArgumentCaptor<Collection> batch = ArgumentCaptor.forClass(Collection.class);
        verify(delegate, times(2)).createBatch(batch.capture());
        Collection secondInvocationValue = batch.getAllValues().get(1);
        assertEquals(0, secondInvocationValue.size());
    }

    @Test
    public void shouldInvokeGetOnDelegateWhenCacheNotPresent() {
        dao.get("aa");

        verify(delegate).get(anyString());
    }

    @Test
    public void shouldUseCacheOnGetWhenCachePresent() {
        dao.create(token);

        Token tokenRes = dao.get(token.getId());

        verifyZeroInteractions(delegate);
        assertEquals(token, tokenRes);
    }

    @Test
    public void shouldInvokeGetAllOnDelegateWhenCacheNotPresent() {
        dao.getTokens();

        verify(delegate).getTokens();
    }

    @Test
    public void shouldUseCacheAndDelegateOnGet() {
        Token tokenOnDb = mock(Token.class);
        when(delegate.getTokens()).thenReturn(Arrays.asList(tokenOnDb));
        dao.create(token);

        List<Token> tokens = dao.getTokens();

        assertEquals(2, tokens.size());
    }

    @Test
    public void shouldInvokeDeleteOnDelegate() {
        dao.create(token);
        scheduledTask.run();

        dao.delete(token.getId());

        verify(delegate).delete(eq(token.getId()));
    }

    @Test
    public void shouldDeleteFromCache() {
        dao.create(token);

        dao.delete(token.getId());

        Token tokenRes = dao.get(token.getId());
        assertNull(tokenRes);

    }
}
