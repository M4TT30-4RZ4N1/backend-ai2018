package it.polito.ai.lab3.service.model;

import org.springframework.data.annotation.Id;

import java.util.Objects;
public class Position {
    @Id
    public long id;
    public double lat, lng;

    public Position(){}

    public Position(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Double.compare(position.lat, lat) == 0 &&
                Double.compare(position.lng, lng) == 0;
    }

    @Override
    public int hashCode() {

        return Objects.hash(lat, lng);
    }
}
