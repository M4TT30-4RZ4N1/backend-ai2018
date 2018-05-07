package it.polito.ai.lab3.security;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    /**
     * Cerca gli utenti nel db
     */
    Optional<User> findByUsername(String userName);

}