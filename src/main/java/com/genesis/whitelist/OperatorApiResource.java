package com.genesis.whitelist;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.jboss.logging.Logger;

@Path("/operator")
public class OperatorApiResource implements OperatorApi {

    private static final Logger LOG = Logger.getLogger(OperatorApiResource.class);

    @Override
    public Response addIps(String operatorCode, List<@Size(min = 1, max = 45) String> requestBody) {
        LOG.info("addIps called for operator " + operatorCode + " : " + requestBody);
        return Response.ok().header("test", "addIps: " + operatorCode).build();
    }

    @Override
    public Response addOperator(com.genesis.whitelist.model.Operator operator) {
        LOG.info("addOperator called for operator " + operator.getCode());
        return Response.ok().header("test", "addOperator: " + operator.getCode()).build();
    }

    @Override
    public Response getOperatorIpList(String operatorCode) {
        LOG.info("getOperatorIpList called for operator " + operatorCode);
        return Response.ok().header("test", "getOperatorIpList: " + operatorCode).build();
    }

    @Override
    public Response getOperators() {
        LOG.info("getOperators called");
        return Response.ok().header("test", "getOperators").build();
    }
}
