package it.polito.ai.lab3.service;

import it.polito.ai.lab3.service.repositories.PositionRepository;
import it.polito.ai.lab3.service.model.TimedPosition;
import it.polito.ai.lab3.service.repositories.PositionRepositoryImpl;
import it.polito.ai.lab3.service.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

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
        first = positionRepositoryImpl.findLastPositionImpl(user);
        if(first == null) {
            if(validator.validateFirst(p)) {
                addPosition( p);
            }
        }
        else{
            if(validator.validateSequence(first, p)) {
                addPosition(p);
            }
        }

    }

    @PreAuthorize("hasRole( 'USER' )")
    public List<TimedPosition> getPositions(String user){
        List<TimedPosition> res = new ArrayList<>();
        res.addAll(positionRepository.findByUser(user));
        return res;
    }

    @PreAuthorize("hasRole( 'USER' )")
    public List<TimedPosition> getPositionInInterval(String user, Date after, Date before){
        List<TimedPosition> res = new ArrayList<>();
        res.addAll(positionRepository.findByUserAndTimestampBetween(user, after.getTime(), before.getTime()));
        return res;
    }
    @PreAuthorize("hasRole( 'CUSTOMER' )")
    public List<TimedPosition> getPositionInIntervalInPolygon(Date after, Date before){
        List<TimedPosition> res = new ArrayList<>();
        //res.addAll(positionRepository.findByUserIdAndTimestampAfterAndTimestampBeforeafter.getTime(), before.getTime()));
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
