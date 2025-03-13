package com.gn.whitelist.configs;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class CorsConfig {
    @ConfigProperty(name = "quarkus.http.cors.origins")
    String origins;

    @ConfigProperty(name = "quarkus.http.cors.methods")
    String methods;

    @ConfigProperty(name = "quarkus.http.cors.headers")
    String headers;

    @ConfigProperty(name = "quarkus.http.cors.access-control-max-age")
    String maxAge;

    @ConfigProperty(name = "quarkus.http.cors.access-control-allow-credentials")
    boolean allowCredentials;

    public String getOrigins() {
        return origins;
    }

    public String getMethods() {
        return methods;
    }

    public String getHeaders() {
        return headers;
    }

    public String getMaxAge() {
        return maxAge;
    }

    public boolean isAllowCredentials() {
        return allowCredentials;
    }
}