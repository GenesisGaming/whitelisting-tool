package com.genesis.whitelist;

import com.genesis.whitelist.exceptions.OperatorAlreadyExistsException;
import com.genesis.whitelist.exceptions.OperatorMissingException;
import com.genesis.whitelist.model.UpdateIpsRequest;
import com.genesis.whitelist.model.Operator;
import com.genesis.whitelist.services.GitService;
import com.genesis.whitelist.services.impl.GitServiceImpl;
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

    private static final Logger LOG = Logger.getLogger(OperatorApiResource.class);

    private final com.genesis.whitelist.CorsConfig corsConfig;
    private final GitService gitService;


    public OperatorApiResource(com.genesis.whitelist.CorsConfig corsConfig, GitService gitService) {
        this.corsConfig = corsConfig;
        this.gitService = gitService;
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
        try {
            if (updateIpsRequest.getUpdateType().equals(UpdateIpsRequest.UpdateTypeEnum.ADDITION)) {
                LOG.info("addIps - ADDITION");
                gitService.addNewIPs(operatorCode, updateIpsRequest);
            } else {
                LOG.info("addIps - REMOVAL");
                gitService.removeIPs(operatorCode, updateIpsRequest);
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

        try{
            gitService.addNewOperator(operator);
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

        try{
            var ips = gitService.getOperatorIPs(operatorCode);
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

        return prepareCorsResponse(Response.ok(gitService.getAllOperators())
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
