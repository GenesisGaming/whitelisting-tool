package com.gn.whitelist.dao.git;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix="git")
public interface GitConfig {
    String user();
    String token();
    String url();
    String branch();
    String workingDirectory();
}
