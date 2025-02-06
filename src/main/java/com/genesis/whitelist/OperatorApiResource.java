package com.genesis.whitelist;

import com.genesis.whitelist.model.AddIpsRequest;
import com.genesis.whitelist.model.Operator;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@Path("/operator")
public class OperatorApiResource implements OperatorApi {

    private static final Logger LOG = Logger.getLogger(OperatorApiResource.class);

    @Inject
    OperatorApiMockGenerator mockGenerator;

    @ConfigProperty(name = "operator-api.use-mock", defaultValue = "false")
    boolean useMock;

    @Override
    public Response addIps(String operatorCode, AddIpsRequest addIpsRequest) {
        LOG.info("addIps called for operator " + operatorCode + " : " + addIpsRequest);

        if (useMock) {
            LOG.info("Using mock response for addIps");
            return mockGenerator.mockAddIps(operatorCode, addIpsRequest);
        }

        // Real implementation logic here
        return Response.ok()
            .header("test", "addIps : " + operatorCode + " IPs: " + addIpsRequest.getNewIps()).build();
    }

    @Override
    public Response addOperator(Operator operator) {
        LOG.info("addOperator called for operator " + operator.getCode());

        if (useMock) {
            LOG.info("Using mock response for addOperator");
            return mockGenerator.mockAddOperator(operator);
        }

        // Real implementation logic here
        return Response.ok().header("test", "addOperator: " + operator.getCode()).build();
    }

    @Override
    public Response getOperatorIpList(String operatorCode, String whitelistType) {
        LOG.info("getOperatorIpList called for operator " + operatorCode + " with whitelist type " + whitelistType);

        if (useMock) {
            LOG.info("Using mock response for getOperatorIpList");
            return mockGenerator.mockGetOperatorIpList(operatorCode, whitelistType);
        }

        // Real implementation logic here
        return Response.ok().header("test", "getOperatorIpList: " + operatorCode).build();
    }

    @Override
    public Response getOperators() {
        LOG.info("getOperators called");

        if (useMock) {
            LOG.info("Using mock response for getOperators");
            return mockGenerator.mockGetOperators();
        }

        // Real implementation logic here
        return Response.ok().header("test", "getOperators").build();
    }
}
