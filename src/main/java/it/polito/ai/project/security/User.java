package it.polito.ai.project.security;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.UUID;

@Document
public class User implements UserDetails {
    @Id
    private ObjectId _id;
    private  Collection<GrantedAuthority> authorities;
    private  String password;
    @Indexed(unique = true)
    private  String username;
    private  String email;
    private  String activationCode;
    private  String forgottenCode;
    private  boolean accountNonExpired;
    private  boolean accountNonLocked;
    private  boolean credentialsNonExpired;
    private  boolean enabled;
    public  User(){}

    public User(Collection<GrantedAuthority> authorities, String password, String username, boolean accountNonExpired, boolean accountNonLocked, boolean credentialsNonExpired, boolean enabled, String activationCode) {
        this.authorities = authorities;
        this.password = password;
        this.username = username;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
        this.activationCode = activationCode;
    }
    public User(String email, String username, String plainpassword, Collection<GrantedAuthority> grantedAuthorities){
        this.email = email;
        this.username=username;
        this.password= new BCryptPasswordEncoder(4).encode(plainpassword);
        this.accountNonExpired=true;
        this.accountNonLocked=true;
        this.credentialsNonExpired=true;
        this.enabled=false;
        this.authorities= grantedAuthorities;
        this.activationCode =User.generateUniqueCode();
    }
    /**
     * User with password only are preactivated
     * @param username
     * @param plainpassword
     * @param role
     */
    public  User(String username, String plainpassword, String role){
        this.email = "";
        this.username=username;
        this.password= new BCryptPasswordEncoder(4).encode(plainpassword);
        this.accountNonExpired=true;
        this.accountNonLocked=true;
        this.credentialsNonExpired=true;
        this.enabled=true;
        this.authorities= new ArrayList<>();
        this.authorities.add(new SimpleGrantedAuthority(role));
    }
    public String getForgottenCode() {
        return forgottenCode;
    }

    public String generateForgottenCode() {
        this.forgottenCode = User.generateUniqueCode();
        return  this.forgottenCode;
    }
    public void setForgottenCode(String forgottenCode) {
        this.forgottenCode = forgottenCode;
    }
    private static String generateUniqueCode(){
        return Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
    }
    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setAuthorities(Collection<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

}
