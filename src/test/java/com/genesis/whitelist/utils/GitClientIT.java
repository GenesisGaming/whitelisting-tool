package com.genesis.whitelist.utils;


import com.genesis.whitelist.configs.GitConfig;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

//@QuarkusIntegrationTest
// this was part of PoC
class GitClientIT {

    GitClient gitClient;
    GitConfig gitConfig;

    @BeforeEach
    public void setup(@TempDir File tempDir) {
        gitConfig = new TestGitConfig("PatriciuBogatu", "", "https://github.com/PatriciuBogatu/gitj-poc.git", "test", tempDir.getAbsolutePath()) ;
        gitClient = new GitClient(gitConfig);
    }


    @Test
    void pushNewBranchWithNewFile() throws IOException {
        String newFileName = "backend" + System.currentTimeMillis() + ".txt";
        String newFileContent = "Hello World!";

        Files.write(gitClient.getRepoPath().toPath().resolve(newFileName), newFileContent.getBytes());
        gitClient.commitChanges("Test commit" + System.currentTimeMillis(), "Test User", "test@example.com");

        gitClient.pushChanges();
        // the branch appears
    }


    @Test
    void hardResetChangesAndPullFromRemote(){
        gitClient.hardResetBranch(1);
        var before = gitClient.getRepoPath().listFiles().length;
        gitClient.pullChanges();
        var after = gitClient.getRepoPath().listFiles().length;

        Assertions.assertTrue(after > before);
    }


    private static class TestGitConfig implements GitConfig {
        private final String user;
        private final String token;
        private final String url;
        private final String branch;
        private final String workingDirectory;

        TestGitConfig(String user, String token, String url, String branch, String workingDirectory) {
            this.user = user;
            this.token = token;
            this.url = url;
            this.branch = branch;
            this.workingDirectory = workingDirectory;
        }

        @Override
        public String user() {
            return user;
        }

        @Override
        public String token() {
            return token;
        }

        @Override
        public String url() {
            return url;
        }

        @Override
        public String branch() {
            return branch;
        }

        @Override
        public String workingDirectory() {
            return workingDirectory;
        }
    }
}
