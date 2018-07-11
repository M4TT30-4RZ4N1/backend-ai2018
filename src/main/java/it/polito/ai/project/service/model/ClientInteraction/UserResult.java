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
            "#b7c0c7",  // light gray
            "#8B0000",
            "#A52A2A",
            "#B22222",
            "#DC143C",
            "#FF0000",
            "#FF6347",
            "#FF7F50",
            "#CD5C5C",
            "#F08080",
            "#E9967A",
            "#FA8072",
            "#FFA07A",
            "#FF4500",
            "#FF8C00",
            "#FFA500",
            "#FFD700",
            "#B8860B",
            "#DAA520",
            "#EEE8AA",
            "#BDB76B",
            "#F0E68C",
            "#808000",
            "#FFFF00",
            "#9ACD32",
            "#556B2F",
            "#6B8E23",
            "#7CFC00",
            "#7FFF00",
            "#ADFF2F",
            "#006400",
            "#008000",
            "#228B22",
            "#00FF00",
            "#32CD32",
            "#90EE90",
            "#98FB98",
            "#8FBC8F",
            "#00FA9A",
            "#00FF7F",
            "#2E8B57",
            "#66CDAA",
            "#3CB371",
            "#20B2AA",
            "#2F4F4F",
            "#008080",
            "#008B8B",
            "#00FFFF",
            "#00FFFF",
            "#E0FFFF",
            "#00CED1",
            "#40E0D0",
            "#48D1CC",
            "#AFEEEE",
            "#7FFFD4",
            "#B0E0E6",
            "#5F9EA0",
            "#4682B4",
            "#6495ED",
            "#00BFFF",
            "#1E90FF",
            "#ADD8E6",
            "#87CEEB",
            "#87CEFA",
            "#191970",
            "#000080",
            "#00008B",
            "#0000CD",
            "#0000FF",
            "#4169E1",
            "#8A2BE2",
            "#4B0082",
            "#483D8B",
            "#6A5ACD",
            "#7B68EE",
            "#9370DB",
            "#8B008B",
            "#9400D3",
            "#9932CC",
            "#BA55D3",
            "#800080",
            "#D8BFD8",
            "#DDA0DD",
            "#EE82EE",
            "#FF00FF",
            "#DA70D6",
            "#C71585",
            "#DB7093",
            "#FF1493",
            "#FF69B4",
            "#FFB6C1",
            "#FFC0CB",
            "#FAEBD7",
            "#F5F5DC",
            "#FFE4C4",
            "#FFEBCD",
            "#F5DEB3",
            "#FFF8DC",
            "#FFFACD",
            "#FAFAD2",
            "#FFFFE0",
            "#8B4513",
            "#A0522D",
            "#D2691E",
            "#CD853F",
            "#F4A460",
            "#DEB887",
            "#D2B48C",
            "#BC8F8F",
            "#FFE4B5",
            "#FFDEAD",
            "#FFDAB9",
            "#FFE4E1",
            "#FFF0F5",
            "#FAF0E6",
            "#FDF5E6",
            "#FFEFD5",
            "#FFF5EE"
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
