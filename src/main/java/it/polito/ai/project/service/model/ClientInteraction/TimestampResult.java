package it.polito.ai.project.service.model.ClientInteraction;

public class TimestampResult {
    public String user;
    public long timestamp;

    public TimestampResult() {
    }

    public TimestampResult(String user, long timestamp) {
        this.user = user;
        this.timestamp = timestamp;
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
