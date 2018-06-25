package it.polito.ai.project.service.model.ClientInteraction;

public class UserResult {
    public String user;
    public String color;

    public UserResult() {
    }

    public UserResult(String user, String color) {
        this.user = user;
        this.color = color;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
