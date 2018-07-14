package it.polito.ai.project.service.repositories;

import it.polito.ai.project.service.model.TimedPosition;
import it.polito.ai.project.service.model.UserArchive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;
import org.wololo.geojson.Polygon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


@Repository
@RestResource(exported = false)
public class UserArchiveRepositoryImpl {
    private final
    MongoTemplate mongoTemplate;
    @Autowired
    public UserArchiveRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * This method allows to select the last TimedPosition.
     * @param owner username
     * @return the last TimedPosition of a user
     */
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

    /**
     * This method allows to retrieve a user archive list based on a filter.
     * @param jsonpolygon polygon filter
     * @param after start timestamp filter
     * @param before end timestamp filter
     * @param user username filter
     * @return a list of user archives
     */
    public List<UserArchive> getPositionInIntervalInPolygonInUserList(Polygon jsonpolygon, long after, long before, List<String> user) {
        List<UserArchive> result = null;
        result= getArchiveWithPositionInIntervalInPolygonInUserList(jsonpolygon, after, before, user);
        System.out.println("Search result: " + result);
        return result;
    }

    /**
     * This method allows to retrieve a user archive list based on a filter.
     * @param jsonpolygon polygon filter
     * @param after start timestamp filter
     * @param before end timestamp filter
     * @param user username filter
     * @return a list of user archives
     */
    public List<UserArchive> getArchiveWithPositionInIntervalInPolygonInUserList(Polygon jsonpolygon, long after, long before, List<String> user) {
        if(mongoTemplate == null) throw new RuntimeException("Mongo DB not initialized");
        List<UserArchive> result;
        List<Point> springPoints = new ArrayList<>();
        for (int i = 0; i < jsonpolygon.getCoordinates().length; i++) {
            for (int j = 0; j < jsonpolygon.getCoordinates()[i].length; j++) {
                springPoints.add(new Point(jsonpolygon.getCoordinates()[i][j][0], jsonpolygon.getCoordinates()[i][j][1]));
            }
        }
        org.springframework.data.geo.Polygon polygon =
                new org.springframework.data.geo.Polygon(springPoints);
        Query query = new Query();
        TypedAggregation aggregation = newAggregation(
                UserArchive.class,
                match(Criteria.where("deleted").is(false)),
                unwind("content"),
                project("owner","filename","counter","deleted","content"),
                match(Criteria.where("content.point").within(polygon)),
                match(Criteria.where("content.timestamp").gt(after).lt(before)),
                group("filename")
                .first("owner").as("owner")
                .first("filename").as("filename")
                .first("counter").as("counter")
                .first("deleted").as("deleted")
                .push("content").as("content")
        );
        if(user!=null && user.size() > 0){
            aggregation = newAggregation(
                    UserArchive.class,
                    match(Criteria.where("owner").in(user)),
                    match(Criteria.where("deleted").is(false)),
                    unwind("content"),
                    project("owner","filename","counter","deleted","content"),
                    match(Criteria.where("content.point").within(polygon)),
                    match(Criteria.where("content.timestamp").gt(after).lt(before)),
                    group("filename")
                            .first("owner").as("owner")
                            .first("filename").as("filename")
                            .first("counter").as("counter")
                            .first("deleted").as("deleted")
                            .push("content").as("content")
            );
        }

        AggregationResults<UserArchive> aggresult= mongoTemplate.aggregate(aggregation,"archives", UserArchive.class);
        result=aggresult.getMappedResults();
        System.out.println("Search result: " + result);
        return result;
    }
}
