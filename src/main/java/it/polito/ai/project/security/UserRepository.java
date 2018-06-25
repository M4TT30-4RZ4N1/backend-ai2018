package it.polito.ai.project.security;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
@RestResource(exported = false)
public interface UserRepository extends MongoRepository<User, String> {
    /**
     * Cerca gli utenti nel db
     */
    Optional<User> findByUsername(String userName);

}