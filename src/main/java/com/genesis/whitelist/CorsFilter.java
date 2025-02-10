package com.genesis.whitelist;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@PreMatching
@ApplicationScoped
public class CorsFilter implements ContainerResponseFilter {
    private static final Logger LOG = Logger.getLogger(CorsFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext,
        ContainerResponseContext responseContext) {
        // Retrieve the origin from the request
        String origin = requestContext.getHeaderString("Origin");
        String method = requestContext.getMethod();

        LOG.infof("CorsFilter called - Origin: %s, Method: %s", origin, method);

        // Add CORS headers to the response
        responseContext.getHeaders().add("Access-Control-Allow-Origin", origin);
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,HEAD,PATCH");
        responseContext.getHeaders().add("Access-Control-Allow-Headers",
            "Content-Type,Authorization,X-Requested-With,Origin,Accept");
        responseContext.getHeaders().add("Access-Control-Max-Age", "3600");
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
        responseContext.getHeaders().add("Access-Control-Expose-Headers", "Location,Content-Disposition");
    }
}