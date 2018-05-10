package it.polito.ai.lab3.service.repositories;

import it.polito.ai.lab3.service.model.TimedPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RestResource(exported = false)
public class PositionRepositoryImpl{
    
    @Autowired
    MongoTemplate mongoTemplate;

    public TimedPosition findLastPositionImpl(String username){
        if(mongoTemplate == null) throw new RuntimeException("Mongo DB not initialized");

        Query qMaxTimestamp = new Query();
        qMaxTimestamp.addCriteria(Criteria.where("user").is(username));

        List<TimedPosition> l =
                mongoTemplate.find(qMaxTimestamp, TimedPosition.class);

        // calcolo max locale
        TimedPosition timedPosition = l.stream()
                .sorted((p1, p2) -> {
                    if(p1.getTimestamp().getTime() > p2.getTimestamp().getTime())
                        return 1;
                    if(p1.getTimestamp().getTime() == p2.getTimestamp().getTime())
                        return 0;
                    else return -1;
                })
                .findFirst().orElse(null);

        //if(timedPosition != null)
        //System.out.println("max found: " + timedPosition.timestamp);

        return timedPosition;
    }

    public List<TimedPosition> findByUserAndTimestampBetween(String username, long after, long before){
        if(mongoTemplate == null) throw new RuntimeException("Mongo DB not initialized");
        List<TimedPosition> tmp = new ArrayList<>();
        Query query = new Query();
        query.addCriteria(Criteria.where("user").is(username).andOperator(Criteria.where("timestamp").gte(after).andOperator(Criteria.where("timestamp").lte(before))));
        tmp = mongoTemplate.find(query, TimedPosition.class);
        return tmp;
    }

    public List<TimedPosition> findByUser(String username){
        if(mongoTemplate == null) throw new RuntimeException("Mongo DB not initialized");

        List<TimedPosition> tmp = new ArrayList<>();
        Query query = new Query();
        query.addCriteria(Criteria.where("user").is(username));
        tmp = mongoTemplate.find(query, TimedPosition.class);
        return tmp;
    }

    public List<TimedPosition> getPositionInIntervalInPolygon(List<GeoJsonPoint> jsonPoints, long after, long before){

        List<TimedPosition> result = null;

        List<Point> points = jsonPoints.stream()
                                        .map(j -> new Point(j.getX(), j.getY()))
                                        .collect(Collectors.toList());
        // assume that the first and the last points are equal
        Polygon polygon = new Polygon(points);

        Query query = new Query();
        query.addCriteria(Criteria.where("timestamp").gte(after)
                .andOperator(Criteria.where("timestamp").lte(before))
                .andOperator(Criteria.where("point").within(polygon)));

        result= mongoTemplate.find(query, TimedPosition.class);

        return result;
    }
}
