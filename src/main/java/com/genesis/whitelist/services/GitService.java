package com.genesis.whitelist.services;

import com.genesis.whitelist.model.Operator;
import com.genesis.whitelist.model.UpdateIpsRequest;

import java.util.List;

public interface GitService {
    List<String> getOperatorIPs(String operatorName);
    List<Operator> getAllOperators();
    void addNewOperator(Operator operator);
    void addNewIPs(String operatorName, UpdateIpsRequest request);
    void removeIPs(String operatorName, UpdateIpsRequest request);
}
