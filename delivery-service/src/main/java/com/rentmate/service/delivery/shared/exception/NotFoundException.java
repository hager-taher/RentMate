package com.rentmate.service.delivery.shared.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String msg) { super(msg); }
}