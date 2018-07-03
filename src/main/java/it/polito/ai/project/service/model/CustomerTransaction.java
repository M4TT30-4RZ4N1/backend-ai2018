package it.polito.ai.project.service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection="transactions")
public class CustomerTransaction {
    @Id
    private String customerId;
    private String userId;

    public CustomerTransaction(String customerId, String userId, String filename) {
        this.customerId = customerId;
        this.userId = userId;
        this.filename = filename;
    }

    private String filename;
    private int nPositions;
    private double price;

    public  CustomerTransaction(){}

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

    @Override
    public String toString() {
        return "CustomerTransaction{" +
                "customerId='" + customerId + '\'' +
                ", userId='" + userId + '\'' +
                ", nPositions=" + nPositions +
                ", price=" + price +
                '}';
    }

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

    @Override
    public int hashCode() {

        return Objects.hash(customerId, userId, filename, nPositions, price);
    }
}
