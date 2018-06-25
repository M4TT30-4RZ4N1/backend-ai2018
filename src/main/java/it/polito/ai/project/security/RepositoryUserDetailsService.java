package it.polito.ai.project.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RepositoryUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    /**
     * Aggiunge un utente nel DB
     * @param username
     * @param password
     */
    public void addUser(String username, String password){
        userRepository.save(new User(username, password,"ROLE_USER"));
    }


    public void addUser(String email, String username, String password){
        userRepository.save(new User(email, username, password,"ROLE_USER"));
    }
    /**
     * Cerca e carica gli utenti dal database per username
     * @param username username
     * @return L'utente trovato
     * @throws UsernameNotFoundException L'utente non è stato trovato
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            return userOptional.get();
        }


        throw new UsernameNotFoundException(username);
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


}