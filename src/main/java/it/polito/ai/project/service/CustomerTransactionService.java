package it.polito.ai.project.service;

import it.polito.ai.project.service.repositories.CustomerTransactionRepository;
import it.polito.ai.project.service.model.CustomerTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomerTransactionService {
    private final
    CustomerTransactionRepository transactionRepository;

    @Autowired
    public CustomerTransactionService(CustomerTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @PreAuthorize("hasRole( 'CUSTOMER' )")
    public void addTransaction(CustomerTransaction t){
        transactionRepository.save(t);
    }

    @PreAuthorize("hasRole( 'ADMIN' )")
    public List<CustomerTransaction> getTransactions(){
        return new ArrayList<>(transactionRepository.findAll());
    }

    @PreAuthorize("hasRole( 'CUSTOMER' )")
    public List<CustomerTransaction> getTransactionByCustomerAndFilename(String customer, String filename){
        return new ArrayList<>(transactionRepository.findByCustomerIdAndFilename(customer, filename));
    }
}
