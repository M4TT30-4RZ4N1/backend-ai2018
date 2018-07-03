package it.polito.ai.project;

import it.polito.ai.project.service.model.TimedPosition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimedPositionGenerator {
    public static List<TimedPosition> get(){
        ArrayList<TimedPosition> timedpostition=new ArrayList<TimedPosition>();
        timedpostition.add(new TimedPosition(45.010, 45.00, new Date(0).getTime()));
        timedpostition.add(new TimedPosition(45.020, 45.00, new Date(5900).getTime()));
        timedpostition.add(new TimedPosition(45.019, 45.00, new Date(9900).getTime()));
        timedpostition.add(new TimedPosition(45.018, 45.00, new Date(11900).getTime()));
        return timedpostition;
    }
    public static List<TimedPosition> get3(){
        ArrayList<TimedPosition> timedpostition=new ArrayList<TimedPosition>();
        timedpostition.add(new TimedPosition(45.00002, 45.00, new Date(0).getTime()));
        timedpostition.add(new TimedPosition(45.00001, 45.00, new Date(59).getTime()));
        timedpostition.add(new TimedPosition(44.99999, 45.00, new Date(99).getTime()));
        timedpostition.add(new TimedPosition(44.99998, 45.00, new Date(119).getTime()));
        return timedpostition;
    }

    public static List<TimedPosition> get2(){
        ArrayList<TimedPosition> timedpostition=new ArrayList<TimedPosition>();
        timedpostition.add(new TimedPosition(45.010, 45.00, new Date(21900).getTime()));
        timedpostition.add(new TimedPosition(45.020, 45.00, new Date(31900).getTime()));
        timedpostition.add(new TimedPosition(45.019, 45.00, new Date(41900).getTime()));
        timedpostition.add(new TimedPosition(45.018, 45.00, new Date(51900).getTime()));
        return timedpostition;
    }
}
