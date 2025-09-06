package com.example.dweb_App.web.controllers;

import com.example.dweb_App.dto.response.ErrorResponse;
import com.example.dweb_App.exception.BusinessException;
import com.example.dweb_App.exception.EntityNotFoundException;
import com.example.dweb_App.exception.UpdateFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException exception){

        ErrorResponse errorResponse=new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UpdateFailedException.class)
    public ResponseEntity<ErrorResponse> handleUpdateFailure(UpdateFailedException exception){
        ErrorResponse errorResponse=new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<ErrorResponse>(errorResponse,HttpStatus.CONFLICT);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException exception){
        Map<String, String> errors=new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error->{
            errors.put(error.getField(),error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException ex){
        Map<String, Object> response=new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", ex.getHttpStatus().value());
        response.put("error", ex.getHttpStatus().getReasonPhrase());
        response.put("errorCode", ex.getErrorCode());
        response.put("message", ex.getMessage());
        response.put("field", ex.getField());

        return new ResponseEntity<>(response,ex.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException exception){
        Map<String,Object> response=new HashMap<>();

        String parameterName=exception.getName();
        Class<?> requiredType=exception.getRequiredType();
        Object actualValue=exception.getValue();

        String errorMessage=String.format(
                "Parameter '%s' should be of type %s but received value '%s'",
                parameterName,
                requiredType!=null ? requiredType.getSimpleName() : "unknown",
                actualValue
        );

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Invalid Parameter Type");
        response.put("message", errorMessage);
        response.put("parameter", parameterName);
        response.put("expectedType", requiredType != null ? requiredType.getSimpleName() : "unknown");
        response.put("receivedValue", actualValue);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String,Object>> handleDataIntegrityViolation(DataIntegrityViolationException exception){
        Map<String , Object> response=new HashMap<>();

        String errorMessage=extractUserFriendlyMessage(exception);

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("error", "Data Integrity Violation");
        response.put("message", errorMessage);
        response.put("detail", "The operation could not be completed due to data constraints");

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    private String extractUserFriendlyMessage(DataIntegrityViolationException exception){
        String rootMessage=exception.getMostSpecificCause().getMessage();

        if (rootMessage.contains("unique constraint") || rootMessage.contains("Duplicate entry")) {
            return extractUniqueConstraintMessage(rootMessage);
        }
        else if (rootMessage.contains("foreign key constraint")) {
            return extractForeignKeyMessage(rootMessage);
        }
        else if (rootMessage.contains("cannot be null")) {
            return extractNullConstraintMessage(rootMessage);
        }
        else if (rootMessage.contains("data truncation") || rootMessage.contains("too long")) {
            return "The provided data is too long for one or more fields";
        }
        else {
            return "Database constraint violation occurred. Please check your input data.";
        }
    }
    private String extractUniqueConstraintMessage(String message) {
        // Pattern for extracting unique constraint details
        Pattern pattern = Pattern.compile("unique constraint.*?\\[(.*?)\\]|Duplicate entry '(.*?)'");
        Matcher matcher = pattern.matcher(message.toLowerCase());

        if (matcher.find()) {
            String constraintName = matcher.group(1);
            String duplicateValue = matcher.group(2);

            if (constraintName != null) {
                return String.format("A record with these values already exists. Constraint: %s",
                        constraintName);
            }
            if (duplicateValue != null) {
                return String.format("The value '%s' already exists and must be unique",
                        duplicateValue);
            }
        }

        return "This record already exists. Please use unique values.";
    }

    private String extractForeignKeyMessage(String message) {
        Pattern pattern = Pattern.compile("foreign key constraint.*?\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(message.toLowerCase());

        if (matcher.find()) {
            String constraintName = matcher.group(1);
            return String.format("Referenced entity does not exist. Constraint: %s",
                    constraintName);
        }

        return "The referenced entity does not exist or cannot be found.";
    }

    private String extractNullConstraintMessage(String message) {
        Pattern pattern = Pattern.compile("Column '(.*?)' cannot be null");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            String columnName = matcher.group(1);
            return String.format("Field '%s' is required and cannot be empty",
                    convertColumnToFieldName(columnName));
        }

        return "One or more required fields are missing.";
    }

    private String convertColumnToFieldName(String columnName) {
        // Convert database column names to user-friendly field names
        Map<String, String> columnMapping = Map.of(
                "email", "email address",
                "user_name", "username",
                "first_name", "first name",
                "last_name", "last name",
                "phone_number", "phone number"
        );

        return columnMapping.getOrDefault(columnName, columnName);
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String ,Object>> handleMissingParameter(MissingServletRequestParameterException exception){
        Map<String,Object> response=new HashMap<>();

        String parameterType=exception.getParameterType();
        String parameterName=exception.getParameterName();

        String errorMessage=String.format(
                "Required parameter '%s' of type '%s' is missing",
                parameterName,
                parameterType
        );
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Missing Required Parameter");
        response.put("message", errorMessage);
        response.put("parameter", parameterName);
        response.put("parameterType", parameterType);
        response.put("documentation", "/api/docs#required-parameters");

        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CredentialsExpiredException.class)
    public ResponseEntity<?> handleCredentialsExpired(CredentialsExpiredException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "error", "Password change required",
                        "message", "You must change your password before proceeding",
                        "redirect", "/change-password"
                ));
    }

}
