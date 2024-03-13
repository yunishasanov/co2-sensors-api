package com.company.co2sensorsapi.exception;

import com.company.co2sensorsapi.dto.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {


  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<?> resourceNotFoundException(NotFoundException exc) {
    ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.NOT_FOUND.value(),
        exc.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON)
        .body(exceptionResponse);
  }

  @ExceptionHandler(InvalidUuidException.class)
  public ResponseEntity<?> invalidUuidException(InvalidUuidException exc) {
    ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(),
        exc.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
        .body(exceptionResponse);
  }

}
