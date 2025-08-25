package com.example.dweb_App.exception;

public class EntityNotFoundException extends  RuntimeException {

    public EntityNotFoundException(String message){
        super(message);
    }
    public EntityNotFoundException(String message, Throwable cause){
        super(message,cause);
    }

    public EntityNotFoundException(String resourceName, String fieldName, Object fieldValue){
        super(String.format("%s not found with %s: %s", resourceName,fieldName,fieldValue));
    }

    public static EntityNotFoundException of(Class<?> entityClass, String fieldName, Object fieldValue) {
        String resourceName = entityClass.getSimpleName();
        return new EntityNotFoundException(resourceName, fieldName, fieldValue);
    }

}
