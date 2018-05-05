package it.polito.ai.lab3.service.Repositories;

import it.polito.ai.lab3.service.model.CustomerTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;


public interface CustomerTransactionRepository extends MongoRepository<CustomerTransaction, Long> {

}