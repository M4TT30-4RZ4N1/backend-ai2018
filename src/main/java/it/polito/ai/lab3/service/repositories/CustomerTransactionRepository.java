package it.polito.ai.lab3.service.repositories;

import it.polito.ai.lab3.service.model.CustomerTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface CustomerTransactionRepository extends MongoRepository<CustomerTransaction, Long> {

}