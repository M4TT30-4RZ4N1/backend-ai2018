package it.polito.ai.project;

import it.polito.ai.project.service.PositionService;
import it.polito.ai.project.service.model.ClientInteraction.SearchResult;
import it.polito.ai.project.service.model.TimedPosition;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.wololo.geojson.Polygon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Lab3ApplicationTests {

	@Autowired
	PositionService positionService;
	@Test
	public void contextLoads() {
	}
	@Test
	public  void  PositionServiceAproxResearch(){
		ArrayList<String> users=new ArrayList<String>();
		users.add("user1");
		users.add("user2");
		double coordinates[][][]={{{0,0},{0,70},{70,70},{70,0},{0,0}}};
		SearchResult res= positionService.getApproximatePositionInIntervalInPolygonInUserList(new Polygon(coordinates),new Date(),new Date(0),users);
		System.out.println("----------------Positions------------------");
		res.getByPosition().forEach(positionResult -> System.out.println(positionResult.toString()));
		System.out.println("----------------Timestamp------------------");
		res.getByTimestamp().forEach(timestampResult -> System.out.println(timestampResult.toString()));
		System.out.println("----------------User------------------");
		res.getByUser().forEach(userResult -> System.out.println(userResult.toString()));
	}

}
