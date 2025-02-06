package com.genesis.whitelist.services;

import java.util.List;

public interface GitService {
    List<String> getOperatorIPs(String operatorName);
    List<String> getAllOperators();
    void addNewOperator(String operatorName);
    void addNewIPs(String operatorName, List<String> ips);

}
