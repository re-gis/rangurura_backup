package com.backend.proj.exceptions;

public class UnauthorisedException extends RuntimeException {
    public UnauthorisedException(String msg){
        super(msg);    }
}
