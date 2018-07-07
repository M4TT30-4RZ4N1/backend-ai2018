package it.polito.ai.project.service.model.ClientInteraction;

import org.wololo.geojson.Point;

import java.util.Objects;
/**
 * This class is related to PositionResult.
 */
public class PositionResult {
    private String user;
    private Point point;

    public PositionResult() {
    }
    /**
     * This method allows to set a new user and point element.
     * @param user
     * @param point
     */
    public PositionResult(String user, Point point) {
        this.user = user;
        this.point = point;
    }

    /**
     * This method allows to print the PositionResult string.
     */
    @Override
    public String toString() {
        return "PositionResult{" +
                "user='" + user + '\'' +
                ", point=" + point +
                '}';
    }
    /**
     * This method allows to check if two elements PositionResult are equal.
     * @param o
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PositionResult)) return false;
        PositionResult that = (PositionResult) o;
        return this.user.equals(that.user) &&
                Double.compare(this.point.getCoordinates()[0],that.point.getCoordinates()[0])==0&&
                Double.compare(this.point.getCoordinates()[1],that.point.getCoordinates()[1])==0;
    }
    /**
     * This method allows to compute the hash function.
     */
    @Override
    public int hashCode() {
        return Objects.hash(user, point.getCoordinates()[0],point.getCoordinates()[1]);
    }
    /**
     * This method allows to get the user.
     */
    public String getUser() {
        return user;
    }
    /**
     * This method allows to set the user.
     * @param user
     */
    public void setUser(String user) {
        this.user = user;
    }
    /**
     * This method allows to get the point.
     */
    public Point getPoint() {
        return point;
    }
    /**
     * This method allows to set the point.
     * @param point
     */
    public void setPoint(Point point) {
        this.point = point;
    }
}
