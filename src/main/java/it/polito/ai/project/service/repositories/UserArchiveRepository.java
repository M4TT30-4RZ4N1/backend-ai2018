package it.polito.ai.project.service.repositories;

import it.polito.ai.project.service.model.UserArchive;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RestResource(exported = false)
public interface UserArchiveRepository extends MongoRepository<UserArchive, String> {

    List<UserArchive> findByFilename(String filename);
    List<UserArchive> findByOwnerAndDeletedIsFalse(String owner);
}
