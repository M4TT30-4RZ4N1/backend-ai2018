package it.polito.ai.project.service.repositories;

import it.polito.ai.project.service.model.UserArchive;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RestResource(exported = false)
public interface UserArchiveRepository extends MongoRepository<UserArchive, String> {

    UserArchive findByFilename(String filename);

    UserArchive findByFilenameAndDeletedIsFalse(String filename);

    @Query(value="{'filename' : ?0}", fields="{content : 0,id : 0,deleted: 0, counter: 0}")
    UserArchive findByFilenameAndExcludeContentAndExcludeIdAndExcludeCounterAndExcludeDelete(String filename);

    @Query(value="{'owner' : ?0, 'deleted' : false}", fields="{content : 0,id : 0}")
    List<UserArchive> findByOwnerAndDeletedIsFalseAndExcludeContentAndExcludeId(String owner,Pageable pageable);

    @Query(value="{'owner' : ?0, 'deleted' : false}", fields="{content : 0,id : 0}")
    List<UserArchive> findByOwnerAndDeletedIsFalseAndExcludeContentAndExcludeId(String owner);

    List<UserArchive> findByOwnerAndDeletedIsFalse(String user);

    List<UserArchive> findAll();
}
