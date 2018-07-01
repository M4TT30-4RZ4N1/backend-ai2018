package it.polito.ai.project.rest;

import it.polito.ai.project.security.RepositoryUserDetailsService;
import it.polito.ai.project.service.model.ClientInteraction.RegistrationDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/guest")
public class RestRegistrationController {
    /**
     * Repository degli utenti
     */
    private final RepositoryUserDetailsService userDetailsService;

    @Autowired
    public RestRegistrationController(RepositoryUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @RequestMapping(value="/checkUser", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    String usernameProbe(@Param("username") String username) {
        try{
            // lancia un'eccezione se l'utente non esiste
            userDetailsService.loadUserByUsername(username);
            throw new RuntimeException("Utente già presente");
        }catch(UsernameNotFoundException e){
            return username;
        }
    }
    @RequestMapping(value="/register", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody
    void register(@RequestBody RegistrationDetails details) {
        try{
            // ripete il controllo sull'esistenza dell'utente
            userDetailsService.loadUserByUsername(details.getUsername());
            throw new RuntimeException("Utente già presente");
        }catch(UsernameNotFoundException e){
            try {
                userDetailsService.addUser(details.getEmail(), details.getUsername(), details.getPassword());
            }catch(Throwable e2){
                throw new RuntimeException("Errore nell'inserimento dell'utente");
            }
            return;
        }
    }
}
