package it.polito.ai.lab3.service.repositories;

import it.polito.ai.lab3.service.model.TimedPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public abstract class PositionRepositoryImpl implements PositionRepository{
    
    @Autowired
    MongoTemplate mongoTemplate;

    public TimedPosition findLastPositionImpl(long userId){
        if(mongoTemplate == null) throw new RuntimeException("Mongo DB not initialized");
        Query qMaxTimestamp = new Query();
        qMaxTimestamp.addCriteria(Criteria.where("userId").is(userId));
        List<TimedPosition> l =
                mongoTemplate.find(qMaxTimestamp, TimedPosition.class);
        TimedPosition timedPosition = l.stream()
                .sorted((p1, p2) -> {
                    if(p1.getTimestamp().getTime() > p2.getTimestamp().getTime())
                        return 1;
                    if(p1.getTimestamp().getTime() == p2.getTimestamp().getTime())
                        return 0;
                    else return -1;
                })
                .findFirst().orElse(null);
        return timedPosition;
    }
        
}
