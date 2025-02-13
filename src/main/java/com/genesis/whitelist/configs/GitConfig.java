package com.genesis.whitelist.configs;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.ConfigMapping;
import jakarta.enterprise.context.ApplicationScoped;

@ConfigMapping(prefix="git")
public interface GitConfig {
    String user();
    String token();
    String url();
    String branch();
    String workingDirectory();
}
