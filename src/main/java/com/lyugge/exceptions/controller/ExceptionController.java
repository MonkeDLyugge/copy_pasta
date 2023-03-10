package com.lyugge.exceptions.controller;

import com.lyugge.api.response.ErrorResponse;
import com.lyugge.exceptions.ExpirationTimeExceededException;
import com.lyugge.exceptions.PasteNotFoundException;
import com.lyugge.exceptions.WrongArgumentException;
import lombok.extern.log4j.Log4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j
@RestControllerAdvice
public class ExceptionController {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WrongArgumentException.class)
    public ErrorResponse onIncorrectParameters(WrongArgumentException exception) {
        log.error("Wrong arguments have taken. " + exception.getMessage());
        return new ErrorResponse("Incorrect parameters", exception.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PasteNotFoundException.class)
    public ErrorResponse onNotFound(PasteNotFoundException exception) {
        log.error("Wrong arguments have taken. " + exception.getMessage());
        return new ErrorResponse("Paste not found", exception.getMessage());
    }

    @ResponseStatus(HttpStatus.GONE)
    @ExceptionHandler(ExpirationTimeExceededException.class)
    public ErrorResponse onNotFound(ExpirationTimeExceededException exception) {
        log.error("Paste expiration time exceeded. " + exception.getMessage());
        return new ErrorResponse("Expiration time exceeded", exception.getMessage());
    }
}
