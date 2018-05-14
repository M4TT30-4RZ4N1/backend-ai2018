package it.polito.ai.lab3.service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="transactions")
public class CustomerTransaction {
    @Id
    private String customerId;
    private String userId;
    private int nPositions;
    private double price;

    public  CustomerTransaction(){}

    public CustomerTransaction(int nPositions, double price) {
        this.nPositions = nPositions;
        this.price = price;
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
}
