package com.genesis.whitelist;

import com.genesis.whitelist.exceptions.OperatorAlreadyExistsException;
import com.genesis.whitelist.exceptions.OperatorMissingException;
import com.genesis.whitelist.model.Operator;
import com.genesis.whitelist.model.UpdateIpsRequest;
import com.genesis.whitelist.services.GitService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@Path("/operator")
public class OperatorApiResource implements OperatorApi {

    private static final Logger LOG = Logger.getLogger(OperatorApiResource.class);

    GitService gitService;
    @ConfigProperty(name = "operator-api.use-mock", defaultValue = "false")
    boolean useMock;


    public OperatorApiResource(GitService gitService){
        this.gitService = gitService;
    }

    @Override
    public Response updateIps(String operatorCode, UpdateIpsRequest updateIpsRequest) {
        try {
            if(updateIpsRequest.getUpdateType().equals(UpdateIpsRequest.UpdateTypeEnum.ADDITION)){
                LOG.info("updateIps addition called for operator " + operatorCode);
                gitService.addNewIPs(operatorCode, updateIpsRequest);
            }else{
                LOG.info("updateIps removal called for operator " + operatorCode);
                gitService.removeIPs(operatorCode, updateIpsRequest);
            }
            return Response.ok().build();
        }catch (OperatorMissingException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }


    @Override
    public Response addOperator(Operator operator) {
        LOG.info("addOperator called for operator " + operator.getCode());

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

        try {
            return Response.ok(gitService.getOperatorIPs(operatorCode)).build();
        }catch(OperatorMissingException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Override
    public Response getOperators() {
        LOG.info("getOperators called");

        return Response.ok(gitService.getAllOperators()).build();
    }
}
