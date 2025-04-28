package com.example.financetracker.exception;

import lombok.Data;

@Data
public class FieldValidationError {
    private String field;
    private String message;

    public FieldValidationError(String field, String message) {
        this.field = field;
        this.message = message;
    }
}
