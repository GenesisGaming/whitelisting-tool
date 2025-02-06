package com.genesis.whitelist.utils;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;

@ApplicationScoped
public class Repository {
    private final String username;
    private final String token;
    private final String url;
    private final File repoPath;

    public Repository(String username, String password, String url, File repoPath){
        this.username = username;
        this.token = password;
        this.url = url;
        this.repoPath = repoPath;
    }

    public void cloneRepository() {
        try {
            CloneCommand cloneCommand = Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(repoPath);

            CredentialsProvider provider = new UsernamePasswordCredentialsProvider(username, token);
            cloneCommand.setCredentialsProvider(provider);
            Git git = cloneCommand.call();
            git.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to clone the repo", e);
        }
    }

    public void commitChanges(String message, String authorName, String authorEmail) {
        try (Git git = Git.open(repoPath)) {
            git.add().addFilepattern(".").call(); // Stage all changes
            git.commit()
                    .setMessage(message)
                    .setAuthor(authorName, authorEmail)
                    .call();
        } catch (Exception e) {
            throw new RuntimeException("Failed to commit changes", e);
        }
    }

    public void pushChanges(String username, String password) {
        try (Git git = Git.open(repoPath)) {
            PushCommand pushCommand = git.push();
            if (username != null && password != null) {
                CredentialsProvider provider = new UsernamePasswordCredentialsProvider(username, password);
                pushCommand.setCredentialsProvider(provider);
            }
            pushCommand.call();
        } catch (Exception e) {
            throw new RuntimeException("Failed to push changes", e);
        }
    }

    public void pullChanges(String username, String password){
        try (Git git = Git.open(repoPath)) {
            PullCommand pullCommand = git.pull();
            if (username != null && password != null) {
                CredentialsProvider provider = new UsernamePasswordCredentialsProvider(username, password);
                pullCommand.setCredentialsProvider(provider);
            }
            pullCommand.call();
        } catch (Exception e) {
            throw new RuntimeException("Failed to push changes", e);
        }
    }

    public void checkStatus(String username, String password) {
        try (Git git = Git.open(repoPath)) {
            StatusCommand statusCommand = git.status();
            if (username != null && password != null) {
                statusCommand.call();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to push changes", e);
        }

    }


    public File getRepoPath(){
        return repoPath;
    }
}
