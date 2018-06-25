package it.polito.ai.project.service.repositories;

import it.polito.ai.project.service.model.CustomerTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RestResource(exported = false)
public interface CustomerTransactionRepository extends MongoRepository<CustomerTransaction, String> {

    List<CustomerTransaction> findByCustomerId(String customer);
}