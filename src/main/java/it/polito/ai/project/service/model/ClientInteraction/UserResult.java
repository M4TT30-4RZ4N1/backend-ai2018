package it.polito.ai.project.service.model.ClientInteraction;

import java.util.Objects;
import java.util.Random;

public class UserResult {
    public String user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserResult)) return false;
        UserResult that = (UserResult) o;
        return Objects.equals(user, that.user);
    }

    private String color;

    public UserResult() {
    }

    public UserResult(String user, String color) {
        this.user = user;
        this.color = color;
    }
    public UserResult(String user) {
        this.user = user;
        Random randomGenerator = new Random();
        int randomNumber = randomGenerator.nextInt(mColors.length);
        this.color = mColors[randomNumber];
    }
    @Override
    public String toString() {
        return "UserResult{" +
                "user='" + user + '\'' +
                ", color='" + color + '\'' +
                '}';
    }



    @Override
    public int hashCode() {

        return Objects.hash(user);
    }
    private static String[] mColors = {
            "#39add1", // light blue
            "#3079ab", // dark blue
            "#c25975", // mauve
            "#e15258", // red
            "#f9845b", // orange
            "#838cc7", // lavender
            "#7d669e", // purple
            "#53bbb4", // aqua
            "#51b46d", // green
            "#e0ab18", // mustard
            "#637a91", // dark gray
            "#f092b0", // pink
            "#b7c0c7"  // light gray
    };
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
