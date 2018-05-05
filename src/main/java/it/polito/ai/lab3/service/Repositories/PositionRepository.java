package it.polito.ai.lab3.service.Repositories;

import it.polito.ai.lab3.service.model.TimedPosition;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionRepository extends MongoRepository<TimedPosition, Long> {

    List<TimedPosition> findByUserId(Long userId);
    List<TimedPosition> findByUserIdAndTimestampAfterAndTimestampBefore(Long userId, Long after, Long before);


    //TimedPosition findLastPosition(@Param("userId") Long userId);
}
