package it.polito.ai.lab3.service.repositories;

import it.polito.ai.lab3.service.model.CustomerTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

@Repository
@RestResource(exported = false)

public interface CustomerTransactionRepository extends MongoRepository<CustomerTransaction, Long> {

}