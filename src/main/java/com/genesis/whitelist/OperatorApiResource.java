package com.genesis.whitelist;
import com.genesis.whitelist.model.AddIpsRequest;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("/operator")
public class OperatorApiResource implements OperatorApi {

    private static final Logger LOG = Logger.getLogger(OperatorApiResource.class);

    @Override
    public Response addIps(String operatorCode, AddIpsRequest addIpsRequest) {
        LOG.info("addIps called for operator " + operatorCode + " : " + addIpsRequest);
        return null;
    }

    @Override
    public Response addOperator(com.genesis.whitelist.model.Operator operator) {
        LOG.info("addOperator called for operator " + operator.getCode());
        return Response.ok().header("test", "addOperator: " + operator.getCode()).build();
    }

    @Override
    public Response getOperatorIpList(String operatorCode, String whitelistType) {
        LOG.info("getOperatorIpList called for operator " + operatorCode + " with whitelist type " + whitelistType);
        return null;
    }

    @Override
    public Response getOperators() {
        LOG.info("getOperators called");
        return Response.ok().header("test", "getOperators").build();
    }
}
