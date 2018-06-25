package it.polito.ai.project.service.repositories;

import it.polito.ai.project.service.model.TimedPosition;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RestResource(exported = false)
public interface PositionRepository extends MongoRepository<TimedPosition, Long> {

    @Override
    List<TimedPosition> findAll();

    //TimedPosition findLastPosition(long userId);
}
