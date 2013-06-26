package com.workshare.micro.api.tokens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.skife.jdbi.v2.sqlobject.BindBean;

import com.workshare.micro.api.tokens.model.Token;
import com.workshare.micro.api.tokens.persistence.dao.TokensDao;

public class CachingTokensDao implements TokensDao {

    private static final long SCHEDULER_MILLISECONDS_DELAY = 1000;

    private final TokensDao delegate;
    private volatile Map<String, Token> cache;

    public CachingTokensDao(TokensDao aDelegate, ScheduledExecutorService aScheduler) {
        this.delegate = aDelegate;
        this.cache = new HashMap<String, Token>();

        Runnable delayedWriter = new Runnable() {
            @Override
            public void run() {
                Map<String, Token> oldCache = cache;
                cache = new HashMap<String, Token>();
                delegate.createBatch(oldCache.values());
            }
        };

        aScheduler.scheduleAtFixedRate(delayedWriter, 0, SCHEDULER_MILLISECONDS_DELAY, TimeUnit.MILLISECONDS);
    }

    @Override
    public Token get(String id) {
        Token token = cache.get(id);
        return (token == null) ? delegate.get(id) : token;
    }

    @Override
    public List<Token> getTokens() {
        List<Token> tokens = new ArrayList<Token>();
        tokens.addAll(delegate.getTokens());
        tokens.addAll(cache.values());
        return tokens;
    }

    @Override
    public void delete(String id) {
        cache.remove(id);
        delegate.delete(id);
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public void create(Token token) {
        cache.put(token.getId(), token);
    }

    // TODO should we implement this for consistency?
    // we are not optimizing this because our APIs will never directly use this
    @Override
    public void createBatch(@BindBean Collection<Token> tokens) {
        delegate.createBatch(tokens);
    }

}
