package it.polito.ai.project.service.model.ClientInteraction;

import org.wololo.geojson.Point;

public class PositionResult {
    public String user;
    public Point point;

    public PositionResult() {
    }

    public PositionResult(String user, Point point) {
        this.user = user;
        this.point = point;
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
