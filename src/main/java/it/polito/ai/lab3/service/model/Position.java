package it.polito.ai.lab3.service.model;

import org.springframework.data.annotation.Id;

import java.util.Objects;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

public class Position {
    @Id
    public String id;
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    public GeoJsonPoint point;

    public Position(){}

    public Position(double lat, double lng) {
        this.point = new GeoJsonPoint(lng, lat);
    }

    public double getLat() {
        return point.getY();
    }

    public double getLng() {
        return point.getX();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Double.compare(position.getLat(), this.getLat()) == 0 &&
                Double.compare(position.getLng(), this.getLng()) == 0;
    }

    @Override
    public int hashCode() {

        return Objects.hash(getLat(), getLng());
    }
}
