package it.polito.ai.project.service.model.ClientInteraction;

public class BuyArchive {
    public String filename;
    public boolean purchased;

    public BuyArchive() {
    }

    public BuyArchive(String filename, boolean purchased) {
        this.filename = filename;
        this.purchased = purchased;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }
}
