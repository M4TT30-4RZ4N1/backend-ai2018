package it.polito.ai.project.service;

import it.polito.ai.project.service.model.ClientInteraction.PositionResult;
import it.polito.ai.project.service.model.ClientInteraction.SearchResult;
import it.polito.ai.project.service.model.ClientInteraction.TimestampResult;
import it.polito.ai.project.service.model.ClientInteraction.UserResult;
import it.polito.ai.project.service.model.Position;
import it.polito.ai.project.service.model.UserArchive;
import it.polito.ai.project.service.repositories.PositionRepository;
import it.polito.ai.project.service.model.TimedPosition;
import it.polito.ai.project.service.repositories.PositionRepositoryImpl;
import it.polito.ai.project.service.repositories.UserArchiveRepository;
import it.polito.ai.project.service.validator.Validator;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.wololo.geojson.Polygon;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class PositionService {
    private final PositionRepository positionRepository;
    private final PositionRepositoryImpl positionRepositoryImpl;
    private final UserArchiveRepository userArchiveRepository;
    private Validator validator;

    @Autowired
    public PositionService(PositionRepositoryImpl positionRepositoryImpl, PositionRepository positionRepository, UserArchiveRepository userArchiveRepository){
        this.validator = new Validator();
        this.positionRepositoryImpl = positionRepositoryImpl;
        this.positionRepository = positionRepository;
        this.userArchiveRepository = userArchiveRepository;
    }

    @PreAuthorize("hasRole( 'USER' )")
    private void addPosition(TimedPosition p){
        positionRepository.insert(p);
    }

    public synchronized void addToDB(String user, TimedPosition p){

        // associate user to the position
        p.setUser(user);
        TimedPosition first = positionRepositoryImpl.findLastPositionImpl(user);
        if(first == null) {
            if(validator.validateFirst(p)) {
                addPosition(p);
            }
        }
        else{
            //System.out.println("first: " + first.getTimestamp() + " second: "+ p.getTimestamp());
            if(validator.validateSequence(first, p)) {
                //System.out.println("inside IF first: " + first.getTimestamp() + " second: "+ p.getTimestamp());
                addPosition(p);
            }
        }

    }

    @PreAuthorize("hasRole( 'USER' )")
    public List<TimedPosition> getPositions(String user){
        return new ArrayList<>(positionRepositoryImpl.findByUser(user));
    }

    @PreAuthorize("hasRole( 'USER' )")
    public List<TimedPosition> getPositionInInterval(String user, Date after, Date before){
        return new ArrayList<>(positionRepositoryImpl.findByUserAndTimestampBetween(user, after.getTime(), before.getTime()));
    }
    @PreAuthorize("hasRole( 'CUSTOMER' )")
    public List<TimedPosition> getPositionInIntervalInPolygon(Polygon polygon, Date after, Date before){
        return new ArrayList<>(positionRepositoryImpl.getPositionInIntervalInPolygon(polygon, after.getTime(), before.getTime()));
    }

    @PreAuthorize("hasRole( 'CUSTOMER' )")
    public List<TimedPosition> getPositionInIntervalInPolygonInUserList(Polygon polygon, Date after, Date before, List<String> users){
        return new ArrayList<>(positionRepositoryImpl.getApproximatePositionInIntervalInPolygonInUserList(polygon, after.getTime(), before.getTime(), users));
    }

    @PreAuthorize("hasRole( 'CUSTOMER' )")
    public SearchResult getApproximatePositionInIntervalInPolygonInUserList(Polygon polygon, Date after, Date before, List<String> users){
        List<TimedPosition> res = new ArrayList<>(positionRepositoryImpl.getApproximatePositionInIntervalInPolygonInUserList(polygon, after.getTime(), before.getTime(), users));
        SearchResult searchResult=new SearchResult();
        searchResult.byTimestamp=res.stream().map(timedPosition ->new TimestampResult(timedPosition.user, DateUtils.setSeconds(new Date(timedPosition.timestamp*1000), 0).getTime())).distinct()
                                    .sorted(Comparator.comparingLong(TimestampResult::getTimestamp)).collect(Collectors.toList());
        searchResult.byPosition=res.stream().map(timedPosition ->{timedPosition.trimPrecsion(); return new PositionResult(timedPosition.user, timedPosition.point);}).distinct().collect(Collectors.toList());
        searchResult.byUser=res.stream().map(timedPosition -> new UserResult(timedPosition.user)).distinct()
                                    .sorted(Comparator.comparing(UserResult::getUser)).collect(Collectors.toList());
        Collections.shuffle(searchResult.byPosition);
        return searchResult;
    }
    @PreAuthorize("hasRole( 'ADMIN' )")
    public List<TimedPosition> getPositions(){
        return new ArrayList<TimedPosition>(positionRepository.findAll());
    }
}
