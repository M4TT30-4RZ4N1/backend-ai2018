package it.polito.ai.project.rest;

import it.polito.ai.project.security.RepositoryUserDetailsService;
import it.polito.ai.project.service.model.ClientInteraction.RegistrationDetails;
import it.polito.ai.project.service.model.CustomException.DuplicateUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

/**
 * This class is related to the RestRegistrationController, which includes the methods for registration.
 */
@RestController
@RequestMapping("/guest")
public class RestRegistrationController {
    /**
     * Users repository
     */
    private final RepositoryUserDetailsService userDetailsService;

    /**
     * This method allows to generate a RestRegistrationController.
     * @param userDetailsService
     */
    @Autowired
    public RestRegistrationController(RepositoryUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    /**
     * This method allows to check if a username has already been used or not.
     * @param username username to be checked
     * @return a String specifying if the user is already present or not
     */
    @RequestMapping(value="/checkUser/{username}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    String usernameProbe(@PathVariable("username") String username) {
        try{
            // lancia un'eccezione se l'utente non esiste
            userDetailsService.loadUserByUsername(username);
            throw new DuplicateUserException("User already present");
        }catch(UsernameNotFoundException e){
            return username;
        }
    }

    /**
     * This method allows to register a new user.
     * @param details email, username, password
     */
    @RequestMapping(value="/register", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody
    void register(@RequestBody RegistrationDetails details) {
        try{
            // it repeats the check for duplicate username
            userDetailsService.loadUserByUsername(details.getUsername());
            throw new DuplicateUserException("User already present");
        }catch(UsernameNotFoundException e){
            try {
                userDetailsService.addUser(details.getUsername(), details.getPassword());
            }catch(Throwable e2){
                throw new RuntimeException("Error adding the user");
            }
            return;
        }
    }
}
