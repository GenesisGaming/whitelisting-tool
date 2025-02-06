package com.genesis.whitelist.utils;

import com.genesis.whitelist.services.configs.GitConfig;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.File;
import java.io.IOException;

@ApplicationScoped
public class GitClient{
    private final GitConfig gitConfig;

    public GitClient(GitConfig gitConfig, @ConfigProperty(name = "branch") String branch){
        this.gitConfig = gitConfig;
        CredentialsProvider provider = new UsernamePasswordCredentialsProvider(gitConfig.user(), gitConfig.token());

        try {
            Git.cloneRepository()
                    .setURI(gitConfig.url())
                    .setDirectory(new File(gitConfig.workingDirectory()))
                    .setCredentialsProvider(provider)
                    .setBranch(branch) // throws ERROR if does not exist on remote, should be ok
                    .call()
                    .close();

        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }


    public void commitChanges(String message, String authorName, String authorEmail) {
        try(Git git = Git.open(getRepoPath()))  {
            git.add().addFilepattern(".").call(); // Stage all changes
            git.commit()
                    .setMessage(message)
                    .setAuthor(authorName, authorEmail)
                    .call();
        } catch (Exception e) {
            throw new RuntimeException("Failed to commit changes", e);
        }
    }

    public void pushChanges() {
        try (Git git = Git.open(getRepoPath())) {
            PushCommand pushCommand = git.push();
            CredentialsProvider provider = new UsernamePasswordCredentialsProvider(gitConfig.user(), gitConfig.token());
            pushCommand.setCredentialsProvider(provider);
            pushCommand.call();
        } catch (Exception e) {
            throw new RuntimeException("Failed to push changes", e);
        }
    }

    public void pullChanges(){
        try (Git git = Git.open(getRepoPath())) {
            PullCommand pullCommand = git.pull();
            CredentialsProvider provider = new UsernamePasswordCredentialsProvider(gitConfig.user(), gitConfig.token());
            pullCommand.setCredentialsProvider(provider);
            pullCommand.call();
        } catch (Exception e) {
            throw new RuntimeException("Failed to push changes", e);
        }
    }

    public void checkStatus(String username, String password) {
        try (Git git = Git.open(getRepoPath())) {
            StatusCommand statusCommand = git.status();
            statusCommand.call();
        } catch (Exception e) {
            throw new RuntimeException("Failed to push changes", e);
        }

    }


    public void hardResetBranch(int offset){
        try (Git git = Git.open(getRepoPath())) {
        git.reset().setMode(ResetCommand.ResetType.HARD)
                .setRef("HEAD~" + offset)
                .call();
        } catch (Exception e) {
            throw new RuntimeException("Failed to push changes", e);
        }
    }


    public void checkoutBranch(String branchName, boolean create){
        try (Git git = Git.open(getRepoPath())) {
            git.checkout()
                    .setCreateBranch(create)
                    .setName(branchName)
                    .call();
        } catch (IOException e) {
            throw new RuntimeException(e);

        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }


    public File getRepoPath(){
        return new File(gitConfig.workingDirectory());
    }

}
