package com.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class BookExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<String> handleBookNotFoundException(BookNotFoundException bookNotFoundException) {
        return new ResponseEntity<>(bookNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidBookException.class)
    public ResponseEntity<String> handleInvalidBookException(InvalidBookException invalidBookException) {
        return new ResponseEntity<>(invalidBookException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidISBNException.class)
    public ResponseEntity<String> handleInvalidISBNException(InvalidISBNException invalidISBNException) {
        return new ResponseEntity<>(invalidISBNException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidAuthorException.class)
    public ResponseEntity<String> handleInvalidAuthorException(InvalidAuthorException invalidAuthorException) {
        return new ResponseEntity<>(invalidAuthorException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookAlreadyExistException.class)
    public ResponseEntity<String> handleBookAlreadyExistException(BookAlreadyExistException bookAlreadyExistException) {
        return new ResponseEntity<>(bookAlreadyExistException.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NoCopiesAvailableException.class)
    public ResponseEntity<String> handleNoCopiesAvailableException(NoCopiesAvailableException noCopiesAvailableException) {
        return new ResponseEntity<>(noCopiesAvailableException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidTokenException(InvalidTokenException invalidTokenException) {
        return new ResponseEntity<>(invalidTokenException.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<String> handleInvalidUserException(InvalidUserException invalidUserException) {
        return new ResponseEntity<>(invalidUserException.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception exception, WebRequest request) {
        return new ResponseEntity<>("Invalid JWT Token: " + exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}

