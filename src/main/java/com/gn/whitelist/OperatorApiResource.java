package com.gn.whitelist;

import com.gn.whitelist.configs.CorsConfig;
import com.gn.whitelist.exceptions.OperatorAlreadyExistsException;
import com.gn.whitelist.exceptions.OperatorMissingException;
import com.gn.whitelist.model.Operator;
import com.gn.whitelist.model.UpdateIpsRequest;
import com.gn.whitelist.dao.WhitelistingStorageService;
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

    private static final Logger LOG = Logger.getLogger(OperatorApiResource.class);

    private final CorsConfig corsConfig;
    private final WhitelistingStorageService whitelistingStorageService;
    private final OperatorApiMockGenerator mockGenerator;

    @ConfigProperty(name = "operator-api.use-mock")
    boolean useMock;

    public OperatorApiResource(CorsConfig corsConfig, WhitelistingStorageService whitelistingStorageService, OperatorApiMockGenerator mockGenerator) {
        this.corsConfig = corsConfig;
        this.whitelistingStorageService = whitelistingStorageService;
        this.mockGenerator = mockGenerator;
    }

    @OPTIONS
    @Path("/{path:.*}")
    public Response handleCORS(@Context HttpHeaders headers, @Context UriInfo uriInfo) {
        return prepareCorsResponse(Response.ok().build());
    }

    @Override
    public Response updateIps(String operatorCode, UpdateIpsRequest updateIpsRequest) {
        LOG.infof("addIps called - Origin: %s",
            httpHeaders.getHeaderString("Origin"));

        if (useMock) {
            LOG.info("Using mock response for updateIps");
            return prepareCorsResponse(mockGenerator.mockUpdateIps(operatorCode, updateIpsRequest));
        }

        try {
            if (updateIpsRequest.getUpdateType().equals(UpdateIpsRequest.UpdateTypeEnum.ADDITION)) {
                LOG.info("addIps - ADDITION");
                whitelistingStorageService.addNewIPs(operatorCode, updateIpsRequest);
            } else {
                LOG.info("addIps - REMOVAL");
                whitelistingStorageService.removeIPs(operatorCode, updateIpsRequest);
            }
        }catch(OperatorMissingException e){
            LOG.error("Operator was not found in the files list");
            return prepareCorsResponse(Response.status(Response.Status.NOT_FOUND)
                    .build());
        }
        return prepareCorsResponse(Response.ok()
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

        try{
            whitelistingStorageService.addNewOperator(operator);
        }catch (OperatorAlreadyExistsException e){
            LOG.error("Operator already exists in the repository");
            return prepareCorsResponse(Response.status(Response.Status.BAD_REQUEST)
                    .build());
        }
        return prepareCorsResponse(Response.status(Response.Status.CREATED)
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

        try{
            var ips = whitelistingStorageService.getOperatorIPs(operatorCode);
            return prepareCorsResponse(Response.ok(ips)
                    .build());
        }catch (OperatorMissingException e){
            LOG.error("Operator was not found in the files list");
            return prepareCorsResponse(Response.status(Response.Status.NOT_FOUND)
                    .build());
        }
    }

    @Override
    public Response getOperators() {
        LOG.infof("getOperators called - Origin: %s",
            httpHeaders.getHeaderString("Origin"));

        if (useMock) {
            LOG.info("Using mock response for getOperators");
            return prepareCorsResponse(mockGenerator.mockGetOperators());
        }

        return prepareCorsResponse(Response.ok(whitelistingStorageService.getAllOperators())
            .build());
    }

    private Response prepareCorsResponse(Response originalResponse) {
        return Response.fromResponse(originalResponse)
            .header("Access-Control-Allow-Origin", corsConfig.getOrigins())
            .header("Access-Control-Allow-Methods", corsConfig.getMethods())
            .header("Access-Control-Allow-Headers", corsConfig.getHeaders())
            .header("Access-Control-Max-Age", corsConfig.getMaxAge())
            .header("Access-Control-Allow-Credentials", corsConfig.isAllowCredentials())
            .build();
    }

    @Context
    HttpHeaders httpHeaders;
}
