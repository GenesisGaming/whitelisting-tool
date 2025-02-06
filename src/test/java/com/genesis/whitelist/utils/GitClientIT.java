package com.genesis.whitelist.utils;


import com.genesis.whitelist.services.configs.GitConfig;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;

@QuarkusIntegrationTest
class GitClientIT {

    GitClient gitClient;
    GitConfig gitConfig;

    @BeforeEach
    public void setup(@TempDir File tempDir) {
        gitConfig = new TestGitConfig("PatriciuBogatu", "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", "https://github.com/PatriciuBogatu/gitj-poc.git", tempDir.getAbsolutePath()) ;
        gitClient = new GitClient(gitConfig, "test");
    }


    @Test
    void pushNewBranchWithNewFile() throws IOException {
        String newFileName = "backend2" + new Random(100).nextInt() + ".txt";
        String newFileContent = "Hello World!";

        Files.write(gitClient.getRepoPath().toPath().resolve(newFileName), newFileContent.getBytes());
        gitClient.commitChanges("Test commit", "Test User", "test@example.com");

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
        private final String workingDirectory;

        TestGitConfig(String user, String token, String url, String workingDirectory) {
            this.user = user;
            this.token = token;
            this.url = url;
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
        public String workingDirectory() {
            return workingDirectory;
        }
    }
}
