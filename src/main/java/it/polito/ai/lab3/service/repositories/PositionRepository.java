package it.polito.ai.lab3.service.repositories;

import it.polito.ai.lab3.service.model.TimedPosition;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionRepository extends MongoRepository<TimedPosition, Long> {

    List<TimedPosition> findByUserId(long userId);

    List<TimedPosition> findByUserIdAndTimestampBetween(long userId, long after, long before);
    //db.TimedPositions.find().sort({timestamp:-1}).limit(1) for MAX
    //db.TimedPositions.find().sort({timestamp:+1}).limit(1) for MIN

    //TimedPosition findLastPosition(long userId);
}
