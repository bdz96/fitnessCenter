package com.example.FitnessCenterApp.exception;

public class ClientAlreadyHasActiveMembershipException extends RuntimeException {
    public ClientAlreadyHasActiveMembershipException(String message) {
        super(message);
    }
}
