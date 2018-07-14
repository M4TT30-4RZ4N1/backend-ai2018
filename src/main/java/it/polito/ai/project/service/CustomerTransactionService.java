package it.polito.ai.project.service;

import it.polito.ai.project.service.repositories.CustomerTransactionRepository;
import it.polito.ai.project.service.model.CustomerTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is related to the Customer transaction service.
 */
@Component
public class CustomerTransactionService {
    private final
    CustomerTransactionRepository transactionRepository;

    /**
     * This method allows to handle the Invalid User Details Exception.
     * @param transactionRepository the repository of transactions
     */
    @Autowired
    public CustomerTransactionService(CustomerTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * This method allows to add a new transaction to the repository of transactions.
     * @param t new customer transaction
     */
    @PreAuthorize("hasRole( 'CUSTOMER' )")
    public void addTransaction(CustomerTransaction t){
        transactionRepository.save(t);
    }

    /**
     * This method allows to retrieve all the transactions from the repository.
     * @return      the list of customer transactions
     */
    @PreAuthorize("hasRole( 'ADMIN' )")
    public List<CustomerTransaction> getTransactions(){
        return new ArrayList<>(transactionRepository.findAll());
    }

    /**
     * This method allows to retrieve a transaction list from the repository, based on customer and filename.
     * @param customer customer for the research
     * @param filename filename for the research
     * @return  the transaction list based on customer and filename
     */
    @PreAuthorize("hasRole( 'CUSTOMER' )")
    public List<CustomerTransaction> getTransactionByCustomerAndFilename(String customer, String filename){
        return new ArrayList<>(transactionRepository.findByCustomerIdAndFilename(customer, filename));
    }
}
