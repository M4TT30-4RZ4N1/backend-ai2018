package it.polito.ai.lab3.service.Repositories;

import it.polito.ai.lab3.service.model.CustomerTransaction;
import org.springframework.data.repository.CrudRepository;


public interface CustomerTransactionRepository extends CrudRepository<CustomerTransaction, Long> {

}