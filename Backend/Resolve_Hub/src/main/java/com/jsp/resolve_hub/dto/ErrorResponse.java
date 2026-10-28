package com.jsp.resolve_hub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * The single error format used by every API failure (PDF section 20).
 * "errors" is filled only for validation failures (field name -> message).
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String code;
    private Map<String,String> errors;

    /** Shorter constructor for errors that have no field-level details. */
    public ErrorResponse(
            LocalDateTime timestamp,
            int status,
            String error,
            String message,
            String path,
            String code){
        this.timestamp= timestamp;
        this.status = status;
        this.error= error;
        this.message = message;
        this.path = path;
        this.code= code;
    }
}