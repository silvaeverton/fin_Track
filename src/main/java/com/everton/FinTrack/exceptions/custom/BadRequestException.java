package com.everton.FinTrack.exceptions.custom;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
    private final Integer status;

    public BadRequestException(final String message, final Integer status) {
        super(message);
        this.status = status;
    }
}
