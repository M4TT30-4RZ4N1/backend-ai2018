package it.polito.ai.lab3.service.validator;

import it.polito.ai.lab3.service.model.Haversine;
import it.polito.ai.lab3.service.model.TimedPosition;

public class Validator {

    double lat_inf = -90.00;
    double lat_sup = +90.00;
    double long_inf = -180.00;
    double long_sup = +180.00;
    double max_v = 100.00;

    public Validator() {
    }

    // this method validate the first coordinate of the sequence (only data validity)
    public boolean validateFirst(TimedPosition t){
        boolean flag = validateCoordinate(t);
        return flag;
    }

    // this method validate two coordinates in sequence (data validity + sequence validity)
    public boolean validateSequence(TimedPosition t1, TimedPosition t2){

        // check validity of t2 (assume t1 is correct)
        if(!validateCoordinate(t2)){
            return false;
        }
        // check t2 > t1
        if(t2.getTimestamp().getTime() <= t1.getTimestamp().getTime()){
            return false;
        }
        // check distance/time < 100 ms
        double distance = Haversine.distance(t1.getLat(), t1.getLng(), t2.getLat(), t2.getLng());
        long diffTime = t2.getTimestamp().getTime() - t1.getTimestamp().getTime();
        if(diffTime == 0){
            return false;
        }
        double time = diffTime/1000;
        if(distance/time >= max_v){
            return false;
        }

        return true;
    }

    private boolean validateCoordinate(TimedPosition t){

        //check not null and timestamp >=0
        if(t == null || t.getTimestamp().getTime() < 0){
            return false;
        }
        // check -90.00 <= lat <= +90.00  and -180.00 <= long <= +180.00
        if(t.getLat()> lat_sup || t.getLat() < lat_inf){
            return false;
        }
        if(t.getLng()> long_sup || t.getLng() < long_inf){
            return false;
        }

        return true;
    }


}
