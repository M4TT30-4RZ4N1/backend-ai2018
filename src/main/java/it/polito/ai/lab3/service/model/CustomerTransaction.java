package it.polito.ai.lab3.service.model;

import org.springframework.data.annotation.Id;


public class CustomerTransaction {
    @Id
    private Long customerId;
    private Long userId;
    private int nPositions;
    private double price;

    public  CustomerTransaction(){}

    public CustomerTransaction(int nPositions, double price) {
        this.nPositions = nPositions;
        this.price = price;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
