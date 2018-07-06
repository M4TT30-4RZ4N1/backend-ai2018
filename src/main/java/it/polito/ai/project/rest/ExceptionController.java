package it.polito.ai.project.rest;

import it.polito.ai.project.service.model.CustomException.DuplicateUserException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(DuplicateUserException.class)
    public String handleDuplicate() { return "User already present"; }
}
