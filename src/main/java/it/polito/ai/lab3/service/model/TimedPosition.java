package it.polito.ai.lab3.service.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Objects;
@Document(collection = "positions")
public class TimedPosition extends Position {
    public long timestamp;
    public String user;
    public TimedPosition(double lat, double lng, long timestamp) {
        super(lat, lng);
        this.timestamp = timestamp;
    }

    public TimedPosition(double lat, double lng, long timestamp, String user) {
        super(lat, lng);
        this.timestamp = timestamp;
        this.user = user;
    }

    public TimedPosition() {
    }

    public Date getTimestamp() {
        return new Date(timestamp);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user){
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimedPosition)) return false;
        if (!super.equals(o)) return false;
        TimedPosition that = (TimedPosition) o;
        return timestamp == that.timestamp;
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), timestamp);
    }

    @Override
    public String toString() {
        return "TimedPosition{" +
                "latitude=" + retriveLat() +
                "longitude=" + retrieveLng() +
                "timestamp=" + timestamp +
                '}';
    }
}
