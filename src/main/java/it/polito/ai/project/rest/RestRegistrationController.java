package it.polito.ai.project.rest;

import io.swagger.annotations.ApiParam;
import it.polito.ai.project.security.RepositoryUserDetailsService;
import it.polito.ai.project.service.EmailException;
import it.polito.ai.project.service.model.ClientInteraction.RegistrationDetails;
import it.polito.ai.project.service.model.CustomException.DuplicateUserException;
import it.polito.ai.project.service.model.CustomException.InvalidUserDetailsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;

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
    void register(@RequestBody RegistrationDetails details, @RequestHeader(value = "Origin",required = false) String origin,HttpServletRequest request) {

        if(!userDetailsService.validateUser(details.getUsername()) || !userDetailsService.validateEmail(details.getEmail()))
            throw new InvalidUserDetailsException();
        try{
            // it repeats the check for duplicate username
            userDetailsService.loadUserByUsername(details.getUsername());
            throw new DuplicateUserException("User already present");
        }catch(UsernameNotFoundException e){
            try {
                if(details.getEmail()==null||details.getEmail().isEmpty()){
                    userDetailsService.addUser(details.getUsername(),details.getPassword());
                }
                if(origin!=null&&!origin.isEmpty()&&!origin.equals(getBaseUrl(request))) {
                    userDetailsService.addUserNoActive(details.getEmail(), details.getUsername(), details.getPassword(), origin + "/activate/",true);
                }else{
                    userDetailsService.addUserNoActive(details.getEmail(), details.getUsername(), details.getPassword(), getBaseUrl(request) + "/activate/",false);
                }
            }catch(Throwable e2){
                throw new RuntimeException("Error adding the user, Please try later");
            }
        }
    }
    /**
     * This method allows to activate a new user.
     */
    @RequestMapping(value="/activate/{username}/{activationcode}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    void activate(@ApiParam("Username that will be activated") @PathVariable("username") String username, @ApiParam("Activation Code")@PathVariable("activationcode") String activationcode) throws AccessDeniedException {
        userDetailsService.activateAccount(username,activationcode);
    }
    /**
     * This method allows to reset a user password.
     */
    @RequestMapping(value="/reset/{username}/{forgottencode}", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    void reset(@ApiParam("Username of which password will be resetted") @PathVariable("username") String username, @ApiParam("Forgot Password Code")  @PathVariable("forgottencode") String forgottencode,
                @ApiParam("New Password") @RequestBody String password) throws AccessDeniedException {
        userDetailsService.resetPassword(username,forgottencode,password);
    }
    /**
     * This method allows to request a password reset.
     */
    @RequestMapping(value="/forgot/{username}/", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    void forgot(@ApiParam("Username of which password is forgotted")@PathVariable("username") String username, @RequestHeader(value = "Origin",required = false) String origin,HttpServletRequest request) throws EmailException {
        if(origin!=null&&!origin.isEmpty()&&!origin.equals(getBaseUrl(request))){
            userDetailsService.forgotPassword(username,origin+"/reset/",true);
        }else{
            userDetailsService.forgotPassword(username,getBaseUrl(request)+"/reset/",false);
        }
    }
    private String getBaseUrl(HttpServletRequest req) {
        return req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
    }

}
