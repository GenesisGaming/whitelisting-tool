package com.genesis.whitelist.exceptions;

public class OperatorAlreadyExistsException extends RuntimeException{
    public OperatorAlreadyExistsException(String operator){
        super("Operator [" + operator + "] already exists");
    }
}
