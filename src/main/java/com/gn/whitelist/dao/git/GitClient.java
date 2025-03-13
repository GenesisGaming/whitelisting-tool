package com.gn.whitelist.dao.git;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.IOException;

@ApplicationScoped
public class GitClient{
    private final GitConfig gitConfig;
    private static final Logger LOG = Logger.getLogger(GitClient.class);

    public GitClient(GitConfig gitConfig){
        this.gitConfig = gitConfig;
        File workingDirectory = new File(gitConfig.workingDirectory());

        if(workingDirectory.exists() && workingDirectory.listFiles().length > 0) return;

        CredentialsProvider provider = new UsernamePasswordCredentialsProvider(gitConfig.user(), gitConfig.token());

        try {
            Git.cloneRepository()
                    .setURI(gitConfig.url())
                    .setDirectory(new File(gitConfig.workingDirectory()))
                    .setCredentialsProvider(provider)
                    .setBranch(gitConfig.branch()) // throws ERROR if does not exist on remote, should be ok
                    .call()
                    .close();

        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }

        LOG.info("Initialized git client");
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

        LOG.info("Changes commited");
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

        LOG.info("Changes pushed");
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

        LOG.info("Changes pulled");
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
