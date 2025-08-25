package com.example.dweb_App.exception;

public class ConcurrentModificationException extends UpdateFailedException {
    public ConcurrentModificationException(String entityName, Long id){
        super(entityName+" with id "+ id + " was modified concurrently ");
    }
}
