package com.genesis.whitelist.exceptions;

public class OperatorAlreadyExists extends RuntimeException{
    public OperatorAlreadyExists(String operator){
        super("Operator " + operator + "already exists");
    }
}
