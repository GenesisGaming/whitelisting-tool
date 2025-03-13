package com.gn.whitelist.dao;

import com.gn.whitelist.model.Operator;
import com.gn.whitelist.model.UpdateIpsRequest;

import java.util.List;

public interface WhitelistingStorageService {
    List<String> getOperatorIPs(String operatorName);
    List<Operator> getAllOperators();
    void addNewOperator(Operator operator);
    void addNewIPs(String operatorName, UpdateIpsRequest request);
    void removeIPs(String operatorName, UpdateIpsRequest request);
}
