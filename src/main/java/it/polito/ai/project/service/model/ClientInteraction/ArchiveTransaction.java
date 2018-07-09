package it.polito.ai.project.service.model.ClientInteraction;

public class ArchiveTransaction {
    private String filename;
    private Boolean purchased;

    public ArchiveTransaction() {
    }

    public ArchiveTransaction(String filename, Boolean purchased) {
        this.filename = filename;
        this.purchased = purchased;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(Boolean purchased) {
        this.purchased = purchased;
    }
}
