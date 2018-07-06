package it.polito.ai.project.rest;

import it.polito.ai.project.service.model.CustomException.DuplicateUserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<?> handleDuplicate() {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("User already present");
    }
}
