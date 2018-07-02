package it.polito.ai.project;

import it.polito.ai.project.service.model.TimedPosition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimedPositionGenerator {
    public static List<TimedPosition> get(){
        ArrayList<TimedPosition> timedpostition=new ArrayList<TimedPosition>();
        timedpostition.add(new TimedPosition(45.010, 45.00, new Date(0).getTime()));
        timedpostition.add(new TimedPosition(45.020, 45.00, new Date(59).getTime()));
        timedpostition.add(new TimedPosition(45.019, 45.00, new Date(99).getTime()));
        timedpostition.add(new TimedPosition(45.018, 45.00, new Date(119).getTime()));
        return timedpostition;
    }
}
