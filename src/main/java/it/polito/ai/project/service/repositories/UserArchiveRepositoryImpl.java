package it.polito.ai.project.service.repositories;

import com.mongodb.BasicDBObject;
import it.polito.ai.project.service.model.TimedPosition;
import it.polito.ai.project.service.model.UserArchive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
        query.addCriteria(Criteria.where("owner").is(owner));
        query.addCriteria(Criteria.where("deleted").is(false));
        query.with(new Sort(Sort.Direction.DESC, "content.timestamp"));
        query.limit(1);
        UserArchive lastArchive = mongoTemplate.findOne(query, UserArchive.class);
        if (lastArchive == null) return null;
        TimedPosition maxObject = lastArchive.getContent().stream().sorted(Comparator.comparing(TimedPosition::getTimestamp).reversed()).findFirst().get();
        System.out.println(maxObject);
        return maxObject;
    }
}
