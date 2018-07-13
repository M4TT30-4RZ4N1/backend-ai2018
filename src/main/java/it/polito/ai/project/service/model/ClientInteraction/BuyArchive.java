package it.polito.ai.project.service.model.ClientInteraction;

/**
 * This class is related to archives and their purchase.
 */
public class BuyArchive {
    private String filename;
    private boolean purchased;

    public BuyArchive() {
    }

    /**
     * This method allows to store information about an archive and whether it has been purchased.
     * @param filename
     * @param purchased
     */
    public BuyArchive(String filename, boolean purchased) {
        this.filename = filename;
        this.purchased = purchased;
    }

    /**
     * This method allows to get the filename.
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }
    /**
     * This method allows to set the filename.
     * @param filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }
    /**
     * This method allows to check if the archive has been purchased by the user.
     * @return TRUE if the archive has been purchased, otherwise FALSE
     */
    public boolean isPurchased() {
        return purchased;
    }
    /**
     * This method allows to set if the archive has been purchased by the user.
     * @param purchased
     */
    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }
}
