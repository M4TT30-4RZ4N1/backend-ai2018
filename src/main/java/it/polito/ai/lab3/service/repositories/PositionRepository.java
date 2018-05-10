package it.polito.ai.lab3.service.repositories;

import it.polito.ai.lab3.service.model.TimedPosition;
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



    //db.TimedPositions.find().sort({timestamp:-1}).limit(1) for MAX
    //db.TimedPositions.find().sort({timestamp:+1}).limit(1) for MIN

    //TimedPosition findLastPosition(long userId);
}
