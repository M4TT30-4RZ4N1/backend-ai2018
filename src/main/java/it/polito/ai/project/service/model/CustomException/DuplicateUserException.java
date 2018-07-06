package it.polito.ai.project.service.model.CustomException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "User already present")
public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException() {}
    public DuplicateUserException(String message) {
        super(message);
    }
    public DuplicateUserException(Throwable reason) {
        super(reason);
    }

}
