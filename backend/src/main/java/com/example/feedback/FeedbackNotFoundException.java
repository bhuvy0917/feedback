package com.example.feedback;

public class FeedbackNotFoundException extends RuntimeException {
    
    private final String errorCode;
    private final String errorMessage;

    public FeedbackNotFoundException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}

// Made with Bob
