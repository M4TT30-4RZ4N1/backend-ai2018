package it.polito.ai.project.service;

import it.polito.ai.project.service.repositories.PositionRepository;
import it.polito.ai.project.service.model.TimedPosition;
import it.polito.ai.project.service.repositories.PositionRepositoryImpl;
import it.polito.ai.project.service.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.wololo.geojson.Polygon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Component
public class PositionService {
    @Autowired
    PositionRepository positionRepository;
    @Autowired
    PositionRepositoryImpl positionRepositoryImpl;
    Validator validator;
    TimedPosition first = null;

    public PositionService(){
        this.validator = new Validator();

    }

    @PreAuthorize("hasRole( 'USER' )")
    private void addPosition(TimedPosition p){
        positionRepository.insert(p);
    }

    public synchronized void addToDB(String user, TimedPosition p){

        // associate user to the position
        p.setUser(user);
        first = positionRepositoryImpl.findLastPositionImpl(user);
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
        List<TimedPosition> res = new ArrayList<>();
        res.addAll(positionRepositoryImpl.findByUser(user));
        return res;
    }

    @PreAuthorize("hasRole( 'USER' )")
    public List<TimedPosition> getPositionInInterval(String user, Date after, Date before){
        List<TimedPosition> res = new ArrayList<>();
        res.addAll(positionRepositoryImpl.findByUserAndTimestampBetween(user, after.getTime(), before.getTime()));
        return res;
    }
    @PreAuthorize("hasRole( 'CUSTOMER' )")
    public List<TimedPosition> getPositionInIntervalInPolygon(Polygon polygon, Date after, Date before){
        List<TimedPosition> res = new ArrayList<>();
        res.addAll(positionRepositoryImpl.getPositionInIntervalInPolygon(polygon, after.getTime(), before.getTime()));
        return res;
    }

    @PreAuthorize("hasRole( 'ADMIN' )")
    public List<TimedPosition> getPositions(){
        List<TimedPosition> res = new ArrayList<>();
        for(TimedPosition p : positionRepository.findAll()){
            res.add(p);
        }
        return res;
    }
}
