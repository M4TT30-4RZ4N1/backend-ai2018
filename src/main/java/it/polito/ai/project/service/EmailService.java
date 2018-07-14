package it.polito.ai.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class is related to the Email service.
 */
@Component
public class EmailService {
    private final
    SendGridEmailService sendGridEmailService;

    /**
     * This method allows to instantiate a new email service.
     * @param sendGridEmailService
     */
    @Autowired
    public EmailService(SendGridEmailService sendGridEmailService) {
        this.sendGridEmailService = sendGridEmailService;
    }

    /**
     * This method allows to send the reset message to an email address.
     * @param email email of the user
     * @param username username of the user
     * @param forgoturl replace URL
     */
    public void sendResetPasswordEmail(String email, String username, String forgoturl) throws EmailException {
        try{
            String emailcontent= Files.readAllLines(Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("forget.html")).toURI())).stream().collect(Collectors.joining());
            emailcontent=emailcontent.replace("-username-",username).replace("-url-",forgoturl);
            this.sendGridEmailService.sendHTML("admin@r4ffy.info",email,"Reset Password",emailcontent);
        }catch (Exception e){
            throw new EmailException("Bachend Failure: Unable to send Reset Email!");
        }
    }

    /**
     * This method allows to send the activate message to an email address.
     * @param email email of the user
     * @param username username of the user
     * @param activate activate URL
     */
    public void sendActivateEmail(String email, String username, String activate) throws EmailException {
        try {
            String emailcontent = Files.readAllLines(Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("activate.html")).toURI())).stream().collect(Collectors.joining());
            emailcontent = emailcontent.replace("-username-", username).replace("-url-", activate);
            this.sendGridEmailService.sendHTML("admin@marsmarketplace.ga", email, "Activate Account", emailcontent);
        }catch (Exception e){
            throw new EmailException("Backend Failure: Unable to send Activation Email!");
        }
    }
}
