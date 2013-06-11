package com.workshare.service.token;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.workshare.service.token.bo.TokenBO;

@Component
@Path("/token")
public class TokenService {
    @Autowired
    TokenBO tokenBo;

    @GET
    @Path("/test")
    public Response test() {
	String result = tokenBo.test();
	return Response.status(200).entity(result).build();

    }

}