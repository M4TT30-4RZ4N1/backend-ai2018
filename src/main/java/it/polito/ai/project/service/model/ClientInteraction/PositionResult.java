package it.polito.ai.project.service.model.ClientInteraction;

import org.wololo.geojson.Point;

import java.util.Objects;

public class PositionResult {
    private String user;
    private Point point;

    public PositionResult() {
    }

    public PositionResult(String user, Point point) {
        this.user = user;
        this.point = point;
    }

    @Override
    public String toString() {
        return "PositionResult{" +
                "user='" + user + '\'' +
                ", point=" + point +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PositionResult)) return false;
        PositionResult that = (PositionResult) o;
        return this.user.equals(that.user) &&
                Double.compare(this.point.getCoordinates()[0],that.point.getCoordinates()[0])==0&&
                Double.compare(this.point.getCoordinates()[1],that.point.getCoordinates()[1])==0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, point.getCoordinates()[0],point.getCoordinates()[1]);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }
}
