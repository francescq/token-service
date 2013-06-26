package com.workshare.micro.api.tokens.persistence.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.workshare.micro.api.tokens.model.Token;

public class TokenMapperJdbi implements ResultSetMapper<Token> {

    @Override
    public Token map(int index, ResultSet r, StatementContext ctx) throws SQLException {
	final Token token = new Token(r.getString("id"), r.getString("type"), r.getString("content"), r.getTimestamp("expiry_date"), r.getTimestamp("create_date"), r.getString("create_user"));
	return token;
    }
}