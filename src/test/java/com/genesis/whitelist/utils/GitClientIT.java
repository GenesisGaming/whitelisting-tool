package com.genesis.whitelist;


import com.genesis.whitelist.services.configs.GitConfig;
import com.genesis.whitelist.utils.GitClient;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusIntegrationTest
class GitClientIT {

    private GitClient gitClient;
    private String username;
    private String token;
    private String url;
    private GitConfig gitConfig;

    @BeforeEach
    public void setup(@TempDir File tempDir) {
        gitClient = new GitClient(gitConfig);
    }


    @Test
    public void verifyPushNewBranchWithNewFile() throws IOException, GitAPIException {
        String branchName = "test_branch";
        String newFileName = "backend.txt";
        String newFileContent = "Hello World!";


        Files.write(gitClient.getRepoPath().toPath().resolve(newFileName), newFileContent.getBytes());
        gitClient.commitChanges("Test commit", "Test User", "test@example.com");

        try (Git git = Git.open(gitClient.getRepoPath())) {
            git.checkout()
                    .setCreateBranch(true)
                    .setName(branchName)
                    .call();
        }

        gitClient.pushChanges();
    }

}
