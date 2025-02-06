package com.genesis.whitelist;

import com.genesis.whitelist.model.AddIpsRequest;
import com.genesis.whitelist.model.Operator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.jboss.logging.Logger;

@ApplicationScoped
public class OperatorApiMockGenerator {

    private static final Logger LOG = Logger.getLogger(OperatorApiMockGenerator.class);

    public static Response mockAddIps(String operatorCode, AddIpsRequest addIpsRequest) {
        LOG.info("mockAddIps called for operator: " + operatorCode + " and IPs: " + addIpsRequest);
        return Response.ok()
            .entity(Map.of(
                "whitelistType", addIpsRequest.getWhitelistType(),
                "newIps", addIpsRequest.getNewIps()
            ))
            .build();
    }

    public static Response mockAddOperator(Operator operator) {
        LOG.info("mockAddOperator called for operator: " + operator.getCode());

        // Check if the operator code is "EXISTING_OPERATOR"
        if ("EXISTING_OPERATOR".equals(operator.getCode())) {
            // Return a 400 Bad Request with the specified error body
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of(
                    "errorCode", "EXISTING_OPERATOR",
                    "errorMessage", "The new operator already exists in Git"
                ))
                .build();
        }

        // Create a mock response for adding an operator
        return Response.created(URI.create("/operators/" + operator.getCode()))
            .entity(Map.of(
                "code", operator.getCode(),
                "status", "CREATED"
            ))
            .build();
    }

    public static Response mockGetOperatorIpList(String operatorCode, String whitelistType) {
        LOG.info("mockGetOperatorIpList called for operator: " + operatorCode + " with whitelistType: " + whitelistType);

        // Create a mock IP list response
        List<String> mockIps = Arrays.asList(
            "192.168.1.1",
            "10.0.0.1",
            "172.16.0.1"
        );

        return Response.ok()
            .entity(mockIps)
            .build();
    }

    public static Response mockGetOperators() {
        LOG.info("mockGetOperators called");

        // Create a mock list of operators
        List<Operator> mockOperators = Arrays.asList(
            createMockOperator("OP001"),
            createMockOperator("OP002"),
            createMockOperator("OP003")
        );

        return Response.ok()
            .entity(mockOperators)
            .build();
    }

    private static Operator createMockOperator(String code) {
        Operator operator = new Operator();
        operator.setCode(code);
        return operator;
    }
}