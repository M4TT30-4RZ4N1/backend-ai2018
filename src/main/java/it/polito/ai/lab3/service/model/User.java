package it.polito.ai.lab2.model;

public class User {

    private long id;
    private String nameUser;
    private String password;

    public User(long id, String userName, String password) {
        this.id = id;
        this.nameUser = userName;
        this.password = password;		}

    public long getId() {
        return id;
    }

    public String getNameUser() {
        return nameUser;
    }
    public String getPassword() {
        return password;
    }

}