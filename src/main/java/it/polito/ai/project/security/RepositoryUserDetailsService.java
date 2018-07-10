package it.polito.ai.project.security;

import it.polito.ai.project.service.EmailService;
import it.polito.ai.project.service.SendGridEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class RepositoryUserDetailsService implements UserDetailsService {
    private final EmailService emailService;
    private UserRepository userRepository;

    @Autowired
    public RepositoryUserDetailsService(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Aggiunge un utente nel DB
     * @param username
     * @param password
     */
    public void addUser(String username, String password){
        userRepository.save(new User(username, password,"ROLE_USER"));
    }


    public void addUser(String email, String username, String password){
        ArrayList<GrantedAuthority> grantedAuthorities=new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        User user=new User(email, username, password,grantedAuthorities);
        user.setEnabled(true);
        userRepository.save(user);
    }
    public void addUserNoActive(String email, String username, String password,String activateurl){
        ArrayList<GrantedAuthority> grantedAuthorities=new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        User user=new User(email, username, password,grantedAuthorities);
        userRepository.save(user);
        emailService.sendActivateEmail(user.getEmail(),user.getUsername(),activateurl+username+"/"+user.getActivationCode());
    }
    public void activateAccount(String username,String activationcode) throws AccessDeniedException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            throw new UsernameNotFoundException(username);
        }
        User user=userOptional.get();
        if(user.getActivationCode()!=null && !user.getActivationCode().isEmpty() && user.getActivationCode().equals(activationcode)){
            user.setEnabled(true);
            user.setActivationCode(null);
            userRepository.save(user);
        }else{
            throw new AccessDeniedException("Invalid Activation Code");
        }


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


    public void resetPassword(String username, String forgottencode, String password) throws AccessDeniedException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            throw new UsernameNotFoundException(username);
        }
        User user=userOptional.get();
        if(user.getForgottenCode()!=null && !user.getForgottenCode().isEmpty() && user.getForgottenCode().equals(forgottencode)){
            user.setPassword(new BCryptPasswordEncoder(4).encode(password));
            user.setForgottenCode(null);
            userRepository.save(user);
        }else{
            throw new AccessDeniedException("Invalid Forgotten Code");
        }
    }

    public void forgotPassword(String username,String reseturl) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            throw new UsernameNotFoundException(username);
        }
        User user=userOptional.get();
        if(user.getEmail()==null || user.getEmail().isEmpty()){
            throw new RuntimeException("Email not present, Unable to reset password");
        }
        String forgotten=user.generateForgottenCode();
        userRepository.save(user);
        emailService.sendResetPasswordEmail(user.getEmail(),user.getUsername(),reseturl+username+"/"+forgotten);
    }
}