package it.polito.ai.project.service.repositories;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import it.polito.ai.project.service.model.TimedPosition;
import it.polito.ai.project.service.model.UserArchive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.SerializationUtils;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;
import org.wololo.geojson.Polygon;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RestResource(exported = false)
public class PositionRepositoryImpl{
    
    @Autowired
    MongoTemplate mongoTemplate;

    public DBObject convert(org.springframework.data.geo.Polygon source) {
        BasicDBList list = new BasicDBList();
        for (Point point : source.getPoints())
        {
            list.add(new double[] { point.getX(), point.getY() });
        }
        DBObject object = new BasicDBObject();
        object.put("type", "Polygon");
        object.put("coordinates", list );
        return object;
    }

    public TimedPosition findLastPositionImpl(String username){
        if(mongoTemplate == null) throw new RuntimeException("Mongo DB not initialized");

        Query qMaxTimestamp = new Query();
        qMaxTimestamp.addCriteria(Criteria.where("user").is(username));
        //the ordering should be done in mongo
        //qMaxTimestamp.with(new Sort(Sort.Direction.DESC, "timestamp"));
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

    public List<TimedPosition> getPositionInIntervalInPolygon(Polygon jsonpolygon, long after, long before){
        if(mongoTemplate == null) throw new RuntimeException("Mongo DB not initialized");

        List<TimedPosition> result = null;
        List<Point> springPoints = new ArrayList<>();
        for (int i = 0; i < jsonpolygon.getCoordinates().length; i++) {
            for (int j = 0; j < jsonpolygon.getCoordinates()[i].length; j++) {
                springPoints.add(new Point(jsonpolygon.getCoordinates()[i][j][1], jsonpolygon.getCoordinates()[i][j][0]));
            }
        }
        org.springframework.data.geo.Polygon polygon =
                new org.springframework.data.geo.Polygon(springPoints);
        BasicDBObject timestampQuery = new BasicDBObject();
        timestampQuery.put("timestamp", new BasicDBObject("$gt", after).append("$lt", before));
        BasicDBObject geometryQuery =  new BasicDBObject("point", new BasicDBObject ("$geoWithin", new BasicDBObject("$polygon", convert(polygon))));
        List<BasicDBObject> andQuery = new ArrayList<>();
        andQuery.add(geometryQuery);
        andQuery.add(timestampQuery);
        BasicDBObject finalQuery = new BasicDBObject();
        finalQuery.put("$and", andQuery);
        System.out.println(finalQuery.toString());
        BasicQuery query = new BasicQuery(timestampQuery.toString());
        result= mongoTemplate.find(query, TimedPosition.class);
        System.out.println(result);
        return result;
    }

    public List<TimedPosition> getApproximatePositionInIntervalInPolygonInUserList(Polygon jsonpolygon, long after, long before,List<String> user) {
        if(mongoTemplate == null) throw new RuntimeException("Mongo DB not initialized");
        List<TimedPosition> result = null;
        List<Point> springPoints = new ArrayList<>();
        for (int i = 0; i < jsonpolygon.getCoordinates().length; i++) {
            for (int j = 0; j < jsonpolygon.getCoordinates()[i].length; j++) {
                springPoints.add(new Point(jsonpolygon.getCoordinates()[i][j][1], jsonpolygon.getCoordinates()[i][j][0]));
            }
        }
        org.springframework.data.geo.Polygon polygon =
                new org.springframework.data.geo.Polygon(springPoints);
        Query query = new Query();
        query.addCriteria(Criteria.where("content").elemMatch(
                    Criteria.where("timestamp").gt(before).lt(after)
                            .and("point").within(polygon)));
        query.addCriteria(Criteria.where("owner").in(user));
        result= mongoTemplate.find(query,UserArchive.class)
                .stream().map(userArchive -> {
            userArchive.getContent().forEach(timedPosition -> timedPosition.user=userArchive.getOwner());
            return  userArchive.getContent();
        }).flatMap(List::stream).collect(Collectors.toList());
        System.out.println(result);
        return result;
    }
}
