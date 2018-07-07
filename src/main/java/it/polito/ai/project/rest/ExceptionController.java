package it.polito.ai.project.rest;

import it.polito.ai.project.service.model.CustomException.DuplicateUserException;
import it.polito.ai.project.service.model.CustomException.EmptyArchiveException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;

/**
 * This class is related to the Exception Handlers.
 */
@ControllerAdvice
public class ExceptionController {
    /**
     * This method allows to handle the Duplicate User Exception.
     * @return      the Response Entity
     */
    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<?> handleDuplicate() {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("User already present");
    }
    /**
     * This method allows to handle the Access Denied Exception.
     * @param ex Exception
     * @return      the Response Entity
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleForbidden(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ex.getMessage());
    }
    /**
     * This method allows to handle the Empty Archive Exception.
     * @param ex Exception
     * @return      the Response Entity
     */
    @ExceptionHandler(EmptyArchiveException.class)
    public ResponseEntity<?> handleEmptyArchive(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(ex.getMessage());
    }
}
