package it.polito.ai.project.service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "archives")
public class UserArchive {
    @Id
    public String id;
    private String owner;
    public String filename;
    public int counter;
    private boolean deleted;
    private List<TimedPosition> content;

    public UserArchive() {
    }

    public UserArchive(String owner, String filename, int counter, boolean deleted, List<TimedPosition> content) {
        this.owner = owner;
        this.filename = filename;
        this.counter = counter;
        this.deleted = deleted;
        this.content = content;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public List<TimedPosition> getContent() {
        return content;
    }

    public void setContent(List<TimedPosition> content) {
        this.content = content;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
