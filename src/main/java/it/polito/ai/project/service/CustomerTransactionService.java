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
    @Autowired
    CustomerTransactionRepository transactionRepository;

    @PreAuthorize("hasRole( 'CUSTOMER' )")
    public void addTransaction(CustomerTransaction t){
        transactionRepository.save(t);
    }

    @PreAuthorize("hasRole( 'ADMIN' )")
    public List<CustomerTransaction> getTransactions(){
        List<CustomerTransaction> res = new ArrayList<>();
        for(CustomerTransaction t : transactionRepository.findAll()){
            res.add(t);
        }
        return res;
    }
}
