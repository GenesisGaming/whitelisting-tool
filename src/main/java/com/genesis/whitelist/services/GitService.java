package com.genesis.whitelist.services;

import com.genesis.whitelist.model.AddIpsRequest;
import com.genesis.whitelist.model.Operator;

import java.util.List;

public interface GitService {
    List<String> getOperatorIPs(String operatorName);
    List<Operator> getAllOperators();
    void addNewOperator(Operator operator);
    void addNewIPs(String operatorName, AddIpsRequest request);

}
