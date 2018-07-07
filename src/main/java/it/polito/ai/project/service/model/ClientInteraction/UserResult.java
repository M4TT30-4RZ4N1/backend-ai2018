package it.polito.ai.project.service.model.ClientInteraction;

import java.util.Objects;
import java.util.Random;
/**
 * This class is related to UserResult.
 */
public class UserResult {
    public String user;
    /**
     * This method allows to check if two elements UserResult are equal.
     * @param o
     */
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
    /**
     * This method allows to set a new user and color pair.
     * @param user
     * @param color
     */
    public UserResult(String user, String color) {
        this.user = user;
        this.color = color;
    }
    /**
     * This method allows to set a new user and random color pair.
     * @param user
     */
    public UserResult(String user) {
        this.user = user;
        Random randomGenerator = new Random();
        int randomNumber = randomGenerator.nextInt(mColors.length);
        this.color = mColors[randomNumber];
    }
    /**
     * This method allows to print the UserResult string.
     */
    @Override
    public String toString() {
        return "UserResult{" +
                "user='" + user + '\'' +
                ", color='" + color + '\'' +
                '}';
    }


    /**
     * This method allows to compute the hash function.
     */
    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
    /**
     * This method allows to set a list of colors
     */
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
    /**
     * This method allows to get the user
     */
    public String getUser() {
        return user;
    }
    /**
     * This method allows to set the user
     * @param user
     */
    public void setUser(String user) {
        this.user = user;
    }
    /**
     * This method allows to get the color
     */
    public String getColor() {
        return color;
    }
    /**
     * This method allows to set the color
     * @param color
     */
    public void setColor(String color) {
        this.color = color;
    }
}
