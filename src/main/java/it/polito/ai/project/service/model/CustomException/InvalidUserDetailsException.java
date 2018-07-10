package it.polito.ai.project.service.model.CustomException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Username not valid")
public class InvalidUserDetailsException extends RuntimeException {
    public InvalidUserDetailsException() {}
    public InvalidUserDetailsException(String message) {
        super(message);
    }
    public InvalidUserDetailsException(Throwable reason) {
        super(reason);
    }

}
