package com.genesis.whitelist;

import com.genesis.whitelist.exceptions.OperatorAlreadyExistsException;
import com.genesis.whitelist.exceptions.OperatorMissingException;
import com.genesis.whitelist.model.AddIpsRequest;
import com.genesis.whitelist.model.Operator;
import com.genesis.whitelist.services.GitService;
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
    GitService gitService;
    @ConfigProperty(name = "operator-api.use-mock", defaultValue = "false")
    boolean useMock;


    public OperatorApiResource(GitService gitService){
        this.gitService = gitService;
    }

    @Override
    public Response addIps(String operatorCode, AddIpsRequest addIpsRequest) {
        LOG.info("addIps called for operator " + operatorCode + " : " + addIpsRequest);

        if (useMock) {
            LOG.info("Using mock response for addIps");
            return mockGenerator.mockAddIps(operatorCode, addIpsRequest);
        }
        try {
            gitService.addNewIPs(operatorCode, addIpsRequest);
            return Response.ok().build();
        }catch (OperatorMissingException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Override
    public Response addOperator(Operator operator) {
        LOG.info("addOperator called for operator " + operator.getCode());

        if (useMock) {
            LOG.info("Using mock response for addOperator");
            return mockGenerator.mockAddOperator(operator);
        }

        // Real implementation logic here
        try {
            gitService.addNewOperator(operator);
            return Response.status(Response.Status.CREATED).build();
        }catch(OperatorAlreadyExistsException e){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @Override
    public Response getOperatorIpList(String operatorCode, String whitelistType) {
        LOG.info("getOperatorIpList called for operator " + operatorCode + " with whitelist type " + whitelistType);

        if (useMock) {
            LOG.info("Using mock response for getOperatorIpList");
            return mockGenerator.mockGetOperatorIpList(operatorCode, whitelistType);
        }

        try {
            return Response.ok(gitService.getOperatorIPs(operatorCode)).build();
        }catch(OperatorMissingException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Override
    public Response getOperators() {
        LOG.info("getOperators called");

        if (useMock) {
            LOG.info("Using mock response for getOperators");
            return mockGenerator.mockGetOperators();
        }

        // Real implementation logic here
        return Response.ok(gitService.getAllOperators()).build();
    }
}
