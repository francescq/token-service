package com.workshare.micro.api.tokens;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.workshare.micro.api.tokens.model.Token;
import com.workshare.micro.api.tokens.model.TokenRequest;
import com.workshare.micro.api.tokens.persistence.dao.TokensDao;
import com.workshare.micro.utils.UUIDGenerator;

@Path("/tokens.json")
@Api(value = "/tokens", description = "The tokens API")
@Produces({ "application/json" })
public class TokensApi {

    @Inject
    private TokensDao dao;

    @Inject
    private UUIDGenerator uuids;

    // @Inject
    // private Validator validator;
    // private Set<ConstraintViolation<TokenRequest>> constraintViolations;

    public TokensApi() {
    }

    public TokensApi(TokensDao dao, UUIDGenerator uuids) {
        this();
        this.dao = dao;
        this.uuids = uuids;
        // this.validator = validator;
    }

    private Date addMaxAge(int maxAge) {
        Date date = new Date();
        date.setTime(date.getTime() + maxAge);
        return date;
    }

    @POST
    @Path("/")
    @ApiOperation(value = "Creates a token with the specified content", notes = "The token is created on the system and the url is returned in the location header")
    @ApiErrors(value = { @ApiError(code = 401, reason = "The user could not be authenticated (wrong or no session id)") })
    public Response createToken(@ApiParam(value = "The token to be added in the system", required = true) @Valid TokenRequest body) throws URISyntaxException {
        String id = uuids.generateString();
        String createUser = "createUser";
        String type = body.type;
        String content = body.content;
        Date expiryDate = addMaxAge(body.maxAge);

        Token token = new Token(id, type, content, expiryDate, null, createUser);

        dao.create(token);
        return Response.created(new URI(id)).build();
    }

    @GET
    @Path("/")
    @ApiOperation(value = "Return a list of all the tokens as defined by the filter", notes = "", responseClass = "com.workshare.micro.api.tokens.model.Token", multiValueResponse = true)
    @ApiErrors(value = { @ApiError(code = 403, reason = "Account list not available (at least to you).") })
    public Response getTokens() {
        List<Token> tokensList = dao.getTokens();
        if (tokensList == null) {
            return Response.serverError().build();
        } else {
            return Response.ok(tokensList).build();
        }
    }

    @GET
    @Path("/{id}")
    @ApiOperation(value = "Creates a token with the specified content", notes = "User/Session must be the token creator", responseClass = "com.workshare.micro.api.tokens.model.Token")
    @ApiErrors(value = { @ApiError(code = 401, reason = "The user could not be authenticated (wrong or no session id)") })
    public Response getToken(@ApiParam(value = "id of the token to get", required = true) @PathParam("id") String id) {
        Token token = dao.get(id);
        if (token == null) {
            return Response.status(404).build();
        } else {
            return Response.ok().entity(token).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @ApiOperation(value = "Deletes a token with the specified id", notes = "User/Session must be the token creator")
    @ApiErrors(value = { @ApiError(code = 401, reason = "The user could not be authenticated (wrong or no session id)") })
    public Response deleteToken(@ApiParam(value = "id of the token to delete", required = true) @PathParam("id") String id) {

        dao.delete(id);
        return Response.ok().build();
    }
}
