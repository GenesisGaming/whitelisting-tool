package com.genesis.whitelist.services.impl;

import com.genesis.whitelist.exceptions.OperatorAlreadyExistsException;
import com.genesis.whitelist.exceptions.OperatorMissingException;
import com.genesis.whitelist.services.GitService;
import com.genesis.whitelist.utils.GitClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.jboss.logging.Logger;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

@ApplicationScoped
public class GitServiceImpl implements GitService {

    private GitClient gitClient;
    private final String prefix = "allow ";
    private final String extension = ".conf";
    private static final Logger LOG = Logger.getLogger(GitServiceImpl.class);

    @Inject
    public GitServiceImpl(GitClient gitClient){
        this.gitClient = gitClient;
    }

    public GitServiceImpl(){

    }

    @Override
    public List<String> getOperatorIPs(String operatorName) {
        gitClient.pullChanges();
        File operatorFile = getOperatorFile(operatorName).orElseThrow(() -> new OperatorMissingException(operatorName));
        List<String> ips = new LinkedList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(operatorFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.contains("allow")){
                    ips.add(line.split(" ")[1]);
                }
            }
        } catch (IOException e) {
            LOG.error("Couldn't read the file: {}", e);
        }

        return ips;
    }

    @Override
    public List<String> getAllOperators() {
        gitClient.pullChanges();
        return Arrays.stream(gitClient.getRepoPath().listFiles())
                .map(f -> f.getName().replace(".conf", ""))
                .toList();
    }

    @Override
    public void addNewIPs(String operatorName, List<String> ips) {
        gitClient.pullChanges();
        File operatorFile = getOperatorFile(operatorName).orElseThrow(() -> new OperatorMissingException(operatorName));

        ips.forEach(ip -> {
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(operatorFile, true))) {
                writer.append(prefix)
                        .append(ip)
                        .append("\n");
            }
            catch (IOException e) {
                    LOG.error("Couldn't write to file: {}", e);
                }
        });
        }


    @Override
    public void addNewOperator(String operatorName) {
        gitClient.pullChanges();
        try {
            if (operatorExists(operatorName)) {
                throw new OperatorAlreadyExistsException(operatorName);
            }

            Files.createFile(gitClient.getRepoPath().toPath().resolve(operatorName + ".conf"));
        } catch (IOException e) {
            LOG.error("Couldn't write to file: {}", e);
        }
    }


    private Optional<File> getOperatorFile(String operatorName){
        File repoDir = gitClient.getRepoPath();

        var files = repoDir.listFiles();
        File toRead = null;

        for(var file : files){
            if(file.getName().equals(operatorName + extension)){
                toRead = file;
            }
        }

        return Optional.ofNullable(toRead);
    }

    private boolean operatorExists(String operatorName){
        return Arrays.stream(gitClient.getRepoPath().listFiles())
                .anyMatch(f -> f.getName().equals(operatorName + extension));
    }
}
