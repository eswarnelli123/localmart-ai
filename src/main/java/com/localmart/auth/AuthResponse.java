package com.localmart.auth;

public class AuthResponse {
    private final boolean success;
    private final String message;
    private final String token;

    public AuthResponse(boolean success, String message, String token) {
        this.success = success;
        this.message = message;
        this.token = token;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean success() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String message() {
        return message;
    }

    public String getToken() {
        return token;
    }
}
