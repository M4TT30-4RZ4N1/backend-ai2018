package it.polito.ai.lab3.service.model;

import org.springframework.data.annotation.Id;

import java.util.Objects;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.wololo.geojson.Point;

public class Position {
    @Id
    public String id;
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    public Point point;

    public Position(){}

    public Position(double lat, double lng) {
        double array[] = {lng, lat};
        this.point = new Point(array);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public double retriveLat() {
        return point.getCoordinates()[1];
    }

    public double retrieveLng() {
        return  point.getCoordinates()[0];
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Double.compare(position.retriveLat(), this.retriveLat()) == 0 &&
                Double.compare(position.retrieveLng(), this.retrieveLng()) == 0;
    }

    @Override
    public int hashCode() {

        return Objects.hash(retriveLat(), retrieveLng());
    }
}
