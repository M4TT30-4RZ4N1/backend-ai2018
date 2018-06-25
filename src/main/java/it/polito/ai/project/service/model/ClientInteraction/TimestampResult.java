package it.polito.ai.project.service.model.ClientInteraction;

import java.util.Objects;

public class TimestampResult {
    public String user;
    public long timestamp;

    public TimestampResult() {
    }

    public TimestampResult(String user, long timestamp) {
        this.user = user;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "TimestampResult{" +
                "user='" + user + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimestampResult)) return false;
        TimestampResult that = (TimestampResult) o;
        return Double.compare(this.timestamp,that.timestamp)==0 &&
                this.user.equals(that.user);
    }

    @Override
    public int hashCode() {

        return Objects.hash(user, timestamp);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
