package com.s1.LogiTrack.exception;

public class BusinessRuleException extends RuntimeException{
    public BusinessRuleException(String mensaje){
        super(mensaje);
    }
}
