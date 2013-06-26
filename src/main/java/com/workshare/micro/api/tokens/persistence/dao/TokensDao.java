package com.workshare.micro.api.tokens.persistence.dao;

import java.util.Collection;
import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.workshare.micro.api.tokens.model.Token;

@RegisterMapper(TokenMapperJdbi.class)
public interface TokensDao {
    public static final String create = "INSERT INTO TOKEN (ID, TYPE, CONTENT, EXPIRY_DATE, CREATE_DATE, CREATE_USER) VALUES (:id, :type, :content, :expiryDate, NOW(), :createUser)";
    public static final String selectById = "SELECT ID, TYPE, CONTENT, EXPIRY_DATE, CREATE_DATE, CREATE_USER FROM TOKEN T WHERE T.ID = :id";
    public static final String selectCollection = "SELECT ID, TYPE, CONTENT, EXPIRY_DATE, CREATE_DATE, CREATE_USER FROM TOKEN T WHERE 1=1";
    public static final String delete = "DELETE FROM TOKEN WHERE ID= :id";

    @SqlUpdate(create)
    public void create(@BindBean Token tokens);

    @SqlBatch(create)
    public void createBatch(@BindBean Collection<Token> token);

    @SqlQuery(selectById)
    public Token get(@Bind("id") String id);

    @SqlQuery(selectCollection)
    public List<Token> getTokens();

    @SqlUpdate(delete)
    public void delete(@Bind("id") String id);

    /**
     * close with no args is used to close the connection
     */
    void close();
}