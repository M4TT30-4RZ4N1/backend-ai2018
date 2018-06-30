package it.polito.ai.project.service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "archives")
public class UserArchive {
    @Id
    public String id;



    private String owner;
    @Indexed(unique = true)
    private String filename;
    private int counter;
    private boolean deleted;
    private List<TimedPosition> content;

    public UserArchive() {
    }
    public UserArchive(String owner, String filename, List<TimedPosition> content) {
        this.owner = owner;
        this.filename = filename;
        this.counter = 0;
        this.deleted = false;
        this.content = content;
    }
    public UserArchive(String owner, String filename, int counter, boolean deleted, List<TimedPosition> content) {
        this.owner = owner;
        this.filename = filename;
        this.counter = counter;
        this.deleted = deleted;
        this.content = content;
    }
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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

    @Override
    public String toString() {
        return "UserArchive{" +
                ", owner='" + owner + '\'' +
                ", filename='" + filename + '\'' +
                ", counter=" + counter +
                ", deleted=" + deleted +
                '}';
    }
}
