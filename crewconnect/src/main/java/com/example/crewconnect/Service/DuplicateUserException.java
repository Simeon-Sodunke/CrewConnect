package com.example.crewconnect.Service;

public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException(String message) { super(message); }
}