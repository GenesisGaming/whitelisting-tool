package com.genesis.whitelist;

import com.genesis.whitelist.model.AddIpsRequest;
import com.genesis.whitelist.model.Operator;
import jakarta.inject.Inject;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@Path("/operator")
public class OperatorApiResource implements OperatorApi {

    @ConfigProperty(name = "quarkus.http.cors.origins")
    String corsOrigin;

    @ConfigProperty(name = "quarkus.http.cors.methods")
    String corsMethods;

    @ConfigProperty(name = "quarkus.http.cors.headers")
    String corsHeaders;

    @ConfigProperty(name = "quarkus.http.cors.access-control-max-age")
    String corsMaxAge;

    @ConfigProperty(name = "quarkus.http.cors.access-control-allow-credentials")
    boolean corsAllowCredentials;

    private static final Logger LOG = Logger.getLogger(OperatorApiResource.class);

    @Inject
    OperatorApiMockGenerator mockGenerator;

    @ConfigProperty(name = "operator-api.use-mock", defaultValue = "false")
    boolean useMock;

    @OPTIONS
    @Path("/{path:.*}")
    public Response handleCORS(@Context HttpHeaders headers, @Context UriInfo uriInfo) {
        return prepareCorsResponse(Response.ok().build());
    }

    @Override
    public Response addIps(String operatorCode, AddIpsRequest addIpsRequest) {
        LOG.infof("addIps called - Origin: %s",
            httpHeaders.getHeaderString("Origin"));

        if (useMock) {
            LOG.info("Using mock response for addIps");
            return prepareCorsResponse(mockGenerator.mockAddIps(operatorCode, addIpsRequest));
        }

        // Real implementation logic here
        return prepareCorsResponse(Response.ok()
            .header("test", "addIps : " + operatorCode + " IPs: " + addIpsRequest.getNewIps())
            .build());
    }

    @Override
    public Response addOperator(Operator operator) {
        LOG.infof("addOperator called - Origin: %s",
            httpHeaders.getHeaderString("Origin"));

        if (useMock) {
            LOG.info("Using mock response for addOperator");
            return prepareCorsResponse(mockGenerator.mockAddOperator(operator));
        }

        // Real implementation logic here
        return prepareCorsResponse(Response.ok()
            .header("test", "addOperator: " + operator.getCode())
            .build());
    }

    @Override
    public Response getOperatorIpList(String operatorCode, String whitelistType) {
        LOG.infof("getOperatorIpList called - Origin: %s",
            httpHeaders.getHeaderString("Origin"));

        if (useMock) {
            LOG.info("Using mock response for getOperatorIpList");
            return prepareCorsResponse(mockGenerator.mockGetOperatorIpList(operatorCode, whitelistType));
        }

        // Real implementation logic here
        return prepareCorsResponse(Response.ok()
            .header("test", "getOperatorIpList: " + operatorCode)
            .build());
    }

    @Override
    public Response getOperators() {
        LOG.infof("getOperators called - Origin: %s",
            httpHeaders.getHeaderString("Origin"));

        if (useMock) {
            LOG.info("Using mock response for getOperators");
            return prepareCorsResponse(mockGenerator.mockGetOperators());
        }

        // Real implementation logic here
        return prepareCorsResponse(Response.ok()
            .header("test", "getOperators")
            .build());
    }

    // Helper method to add CORS headers to responses
    private Response prepareCorsResponse(Response originalResponse) {
        return Response.fromResponse(originalResponse)
            .header("Access-Control-Allow-Origin", corsOrigin)
            .header("Access-Control-Allow-Methods", corsMethods)
            .header("Access-Control-Allow-Headers", corsHeaders)
            .header("Access-Control-Max-Age", corsMaxAge)
            .header("Access-Control-Allow-Credentials", corsAllowCredentials)
            .build();
    }

    @Context
    HttpHeaders httpHeaders;
}