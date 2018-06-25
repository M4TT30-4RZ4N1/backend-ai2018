package it.polito.ai.project.service.model.ClientInteraction;

import java.util.Objects;

public class UserResult {
    public String user;
    public String color;

    public UserResult() {
    }

    public UserResult(String user, String color) {
        this.user = user;
        this.color = color;
    }

    @Override
    public String toString() {
        return "UserResult{" +
                "user='" + user + '\'' +
                ", color='" + color + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserResult)) return false;
        UserResult that = (UserResult) o;
        return this.user.equals(that.user) && this.color.equals(that.color);
    }

    @Override
    public int hashCode() {

        return Objects.hash(user, color);
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
