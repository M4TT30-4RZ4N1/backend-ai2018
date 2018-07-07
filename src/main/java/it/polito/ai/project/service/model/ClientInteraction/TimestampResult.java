package it.polito.ai.project.service.model.ClientInteraction;

import java.util.Objects;
/**
 * This class is related to TimestampResult.
 */
public class TimestampResult {
    private String user;
    private long timestamp;

    public TimestampResult() {
    }
    /**
     * This method allows to set a new user and timestamp element.
     * @param user
     * @param timestamp
     */
    public TimestampResult(String user, long timestamp) {
        this.user = user;
        this.timestamp = timestamp;
    }

    /**
     * This method allows to print the TimestampResult string.
     */
    @Override
    public String toString() {
        return "TimestampResult{" +
                "user='" + user + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
    /**
     * This method allows to check if two elements TimestampResult are equal.
     * @param o
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimestampResult)) return false;
        TimestampResult that = (TimestampResult) o;
        return Double.compare(this.timestamp,that.timestamp)==0 &&
                this.user.equals(that.user);
    }

    /**
     * This method allows to compute the hash function.
     */
    @Override
    public int hashCode() {
        return Objects.hash(user, timestamp);
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
     * This method allows to get the timestamp.
     */
    public long getTimestamp() {
        return timestamp;
    }
    /**
     * This method allows to set the timestamp.
     * @param timestamp
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
