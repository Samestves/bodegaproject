package com.example.bodegaproject.utils;

public class ProductServiceException extends Exception {

    // Constructor con mensaje de error y causa (otra excepción)
    public ProductServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
