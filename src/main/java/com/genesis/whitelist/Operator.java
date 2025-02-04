package com.genesis.whitelist;

import jakarta.validation.constraints.Size;
import jakarta.ws.rs.Path;

import jakarta.ws.rs.core.Response;
import java.util.List;


@Path("/operator")
public class Operator implements OperatorApi {

    @Override
    public Response addIps(String operatorCode, List<@Size(min = 1, max = 45) String> requestBody) {
        return Response.ok().header("test", "addIps: " + operatorCode).build();
    }

    @Override
    public Response addOperator(com.genesis.whitelist.model.Operator operator) {
        return Response.ok().header("test", "addOperator: " + operator.getCode()).build();
    }

    @Override
    public Response getOperatorIpList(String operatorCode) {
        return Response.ok().header("test", "getOperatorIpList: " + operatorCode).build();
    }

    @Override
    public Response getOperators() {
        return Response.ok().header("test", "getOperators").build();
    }
}
