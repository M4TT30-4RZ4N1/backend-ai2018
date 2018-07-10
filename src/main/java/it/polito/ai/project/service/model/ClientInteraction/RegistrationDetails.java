package it.polito.ai.project.service.model.ClientInteraction;


/**
 * This class is related to RegistrationDetails.
 */
public class RegistrationDetails {
    private String username;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String email;

    public RegistrationDetails() {
    }

    /**
     * This method allows to set new RegistrationDetails for a user.
     * @param username
     * @param password
     */
    public RegistrationDetails(String email, String username, String password) {
        this.username = username;
        this.password = password;
        this.email=email;
    }

    /**
     * This method allows to get the username.
     */
    public String getUsername() {
        return username;
    }
    /**
     * This method allows to set the username.
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }
    /**
     * This method allows to get the password.
     */
    public String getPassword() {
        return password;
    }
    /**
     * This method allows to set the password.
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
