package it.polito.ai.project.service.repositories;

import com.mongodb.BasicDBObject;
import it.polito.ai.project.service.model.ClientInteraction.ArchiveTransaction;
import it.polito.ai.project.service.model.TimedPosition;
import it.polito.ai.project.service.model.UserArchive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;
import org.wololo.geojson.Polygon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RestResource(exported = false)
public class UserArchiveRepositoryImpl {
    private final
    MongoTemplate mongoTemplate;
    @Autowired
    public UserArchiveRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public TimedPosition findLastPosition(String owner){
        Query query = new Query();
        Boolean b = false;
        query.addCriteria(Criteria.where("owner").is(owner));
        query.addCriteria(Criteria.where("deleted").is(b));
        query.with(new Sort(Sort.Direction.DESC, "content.timestamp"));
        query.limit(1);
        UserArchive lastArchive = mongoTemplate.findOne(query, UserArchive.class);
        System.out.println("Last archive: " + lastArchive);
        if (lastArchive == null) return null;
        TimedPosition maxObject = lastArchive.getContent().stream().max(Comparator.comparing(TimedPosition::getTimestamp)).get();
        System.out.println("Last position: " + maxObject);
        return maxObject;
    }
    public List<UserArchive> getPositionInIntervalInPolygonInUserList(Polygon jsonpolygon, long after, long before, List<String> user) {
        List<UserArchive> result = null;
        result= getArchiveWithPositionInIntervalInPolygonInUserList(jsonpolygon, after, before, user);
        System.out.println("Search result: " + result);
        return result;
    }

    public List<UserArchive> getArchiveWithPositionInIntervalInPolygonInUserList(Polygon jsonpolygon, long after, long before, List<String> user) {
        if(mongoTemplate == null) throw new RuntimeException("Mongo DB not initialized");
        List<UserArchive> result = null;
        List<Point> springPoints = new ArrayList<>();
        for (int i = 0; i < jsonpolygon.getCoordinates().length; i++) {
            for (int j = 0; j < jsonpolygon.getCoordinates()[i].length; j++) {
                springPoints.add(new Point(jsonpolygon.getCoordinates()[i][j][0], jsonpolygon.getCoordinates()[i][j][1]));
            }
        }
        org.springframework.data.geo.Polygon polygon =
                new org.springframework.data.geo.Polygon(springPoints);
        Query query = new Query();
        query.addCriteria(Criteria.where("content").elemMatch(
                Criteria.where("timestamp").gt(after).lt(before)
                        .and("point").within(polygon)));
        query.addCriteria(Criteria.where("deleted").is(false));
        if(user!=null && user.size() > 0)
            query.addCriteria(Criteria.where("owner").in(user));
        result= mongoTemplate.find(query,UserArchive.class);
        System.out.println("Search result: " + result);
        return result;
    }
}
