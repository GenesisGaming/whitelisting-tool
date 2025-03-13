package com.gn.whitelist.exceptions;


public class OperatorMissingException extends RuntimeException{
    public OperatorMissingException(String operator){
        super("Operator [" + operator + "] doesn't have a file in the repo");
    }
}
