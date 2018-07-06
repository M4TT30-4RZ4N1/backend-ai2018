package it.polito.ai.project.service.model.CustomException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "There were no valid position in the archive")
public class EmptyArchiveException extends RuntimeException {
    public EmptyArchiveException() {}
    public EmptyArchiveException(String message) {
        super(message);
    }
    public EmptyArchiveException(Throwable reason) {
        super(reason);
    }

}
