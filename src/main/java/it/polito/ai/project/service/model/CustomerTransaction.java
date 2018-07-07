package it.polito.ai.project.service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;
/**
 * This class is related to the CustomerTransaction.
 */
@Document(collection="transactions")
public class CustomerTransaction {
    @Id
    private String customerId;
    private String userId;
    /**
     * This methods allows to set a new CustomerTransaction.
     * @param customerId
     * @param userId
     * @param filename
     */
    public CustomerTransaction(String customerId, String userId, String filename) {
        this.customerId = customerId;
        this.userId = userId;
        this.filename = filename;
    }

    private String filename;
    private int nPositions;
    private double price;

    public  CustomerTransaction(){}
    /**
     * This methods allows to set a new CustomerTransaction.
     * @param nPositions
     * @param price
     */
    public CustomerTransaction(int nPositions, double price) {
        this.nPositions = nPositions;
        this.price = price;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public int getnPositions() {
        return nPositions;
    }

    public void setnPositions(int nPositions) {
        this.nPositions = nPositions;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    /**
     * This method allows to print the CustomerTransaction string.
     */
    @Override
    public String toString() {
        return "CustomerTransaction{" +
                "customerId='" + customerId + '\'' +
                ", userId='" + userId + '\'' +
                ", nPositions=" + nPositions +
                ", price=" + price +
                '}';
    }
    /**
     * This method allows to check if two elements UserResult are equal.
     * @param o
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerTransaction that = (CustomerTransaction) o;
        return nPositions == that.nPositions &&
                Double.compare(that.price, price) == 0 &&
                Objects.equals(customerId, that.customerId) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(filename, that.filename);
    }

    /**
     * This methods allows to compute the hash function
     */
    @Override
    public int hashCode() {
        return Objects.hash(customerId, userId, filename, nPositions, price);
    }
}
