package com.genesis.whitelist.services.impl;

import com.genesis.whitelist.exceptions.OperatorAlreadyExistsException;
import com.genesis.whitelist.exceptions.OperatorMissingException;
import com.genesis.whitelist.model.Operator;
import com.genesis.whitelist.model.UpdateIpsRequest;
import com.genesis.whitelist.services.GitService;
import com.genesis.whitelist.utils.GitClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

@ApplicationScoped
public class GitServiceImpl implements GitService {
    private GitClient gitClient;
    private File partnersDir;
    private final String FILE_LINE_TEMPLATE = "allow    [IP];";
    private final String FILE_EXTENSION = ".conf";
    private static final Logger LOG = Logger.getLogger(GitServiceImpl.class);

    @Inject
    public GitServiceImpl(GitClient gitClient){
        this.gitClient = gitClient;
        this.partnersDir = new File(gitClient.getRepoPath(), "customers");
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
                    String ip = line.trim().split(" {4}")[1].replace(";", "");
                    ips.add(ip);
                }
            }
        } catch (IOException e) {
            LOG.error("Couldn't read the file: {}", e);
        }

        LOG.info("Read a total of " +  ips.size());

        return ips;
    }

    @Override
    public List<Operator> getAllOperators() throws RuntimeException {
        LOG.info("Pulling changes");
        gitClient.pullChanges();
        final File[] fileList = partnersDir.listFiles();
        if (fileList == null) {
            throw new RuntimeException("Error retrieving the operators' files");
        }
        List<Operator> operators = Arrays.stream(fileList)
                .map(f -> new Operator(f.getName().replace(".conf", "")))
                .toList();

        LOG.info("Total operators fetched - " + operators.size());
        return operators;
    }

    @Override
    public void addNewIPs(String operatorName, UpdateIpsRequest request) {
        var currentIPs = getOperatorIPs(operatorName);
        var ipsToAdd = request.getIps();
        File operatorFile = getOperatorFile(operatorName).get();

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(operatorFile, true))) {
            for(var ip: ipsToAdd){
                if(!currentIPs.contains(ip)){
                    writer.append(FILE_LINE_TEMPLATE.replace("[IP]", ip))
                            .append("\n");

                    currentIPs.add(ip);
                }
            }
        }
        catch (IOException e) {
            LOG.error("Couldn't write to file: {}", e);
        }

        gitClient.commitChanges("Adding new IPs for " + operatorName + ".\n\n" + request.getComments(),
            "Backendapp",
            "backend@genesis.com");
        gitClient.pushChanges();
    }


    @Override
    public void removeIPs(String operatorName, UpdateIpsRequest request){
        var currentIPs = getOperatorIPs(operatorName);
        File operatorFile = getOperatorFile(operatorName).get();

        request.getIps().forEach(currentIPs::remove);

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(operatorFile, false))) {
            for(var ip: currentIPs){
                writer.append(FILE_LINE_TEMPLATE.replace("[IP]", ip))
                        .append("\n");
            }

        } catch (IOException e) {
            LOG.error("Could't write to file: {}", e);
        }

        gitClient.commitChanges("Removing IPs for " + operatorName + ".\n\n" + request.getComments(),
            "Backendapp",
            "backend@genesis.com");
        gitClient.pushChanges();
    }


    @Override
    public void addNewOperator(Operator operator) {
        gitClient.pullChanges();
        try {
            String operatorName = operator.getCode();
            if (operatorExists(operatorName)) {
                throw new OperatorAlreadyExistsException(operatorName);
            }

            Files.createFile(partnersDir.toPath().resolve(operatorName + ".conf"));
        } catch (IOException e) {
            LOG.error("Couldn't write to file: {}", e);
        }

        gitClient.commitChanges("Adding operator " + operator.getCode(), "Backendapp", "backend@genesis.com");
        gitClient.pushChanges();
    }


    private Optional<File> getOperatorFile(String operatorName){
        File toRead = null;
        for(var file : partnersDir.listFiles()){
            if(file.getName().equals(operatorName + FILE_EXTENSION)){
                toRead = file;
            }
        }

        return Optional.ofNullable(toRead);
    }

    private boolean operatorExists(String operatorName){
        return Arrays.stream(partnersDir.listFiles())
                .anyMatch(f -> f.getName().equals(operatorName + FILE_EXTENSION));
    }

}