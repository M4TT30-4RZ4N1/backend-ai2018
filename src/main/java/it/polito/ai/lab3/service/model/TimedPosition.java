package it.polito.ai.lab3.service.model;

import java.util.Date;
import java.util.Objects;

public class TimedPosition extends Position {
    public long timestamp;
    public long userId;
    public TimedPosition(double lat, double lng, long timestamp) {
        super(lat, lng);
        this.timestamp = timestamp;
    }

    public TimedPosition() {
    }

    public Date getTimestamp() {
        return new Date(timestamp);
    }

    public long getUserId() {
        return userId;
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
                "latitude=" + getLat() +
                "longitude=" + getLng() +
                "timestamp=" + timestamp +
                '}';
    }
}
