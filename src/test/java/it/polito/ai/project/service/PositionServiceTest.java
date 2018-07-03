package it.polito.ai.project.service;

import it.polito.ai.project.TimedPositionGenerator;
import it.polito.ai.project.security.User;
import it.polito.ai.project.security.UserRepository;
import it.polito.ai.project.service.model.ClientInteraction.PositionResult;
import it.polito.ai.project.service.model.ClientInteraction.SearchResult;
import it.polito.ai.project.service.model.ClientInteraction.TimestampResult;
import it.polito.ai.project.service.model.ClientInteraction.UserResult;
import it.polito.ai.project.service.model.TimedPosition;
import it.polito.ai.project.service.model.UserArchive;
import it.polito.ai.project.service.repositories.UserArchiveRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.wololo.geojson.Point;
import org.wololo.geojson.Polygon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class PositionServiceTest {

	@Autowired
    PositionService positionService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserArchiveRepository userArchiveRepository;
    @Before
    public void multipleInit() {
        userRepository.deleteAll();
        userArchiveRepository.deleteAll();
    }
	@Test
	public  void  PositionServiceAproxResearch(){
        userRepository.save(new User("user1","testpassword","ROLE_USER"));
        userRepository.save(new User("user2","testpassword","ROLE_USER"));
        userRepository.save(new User("user3","testpassword","ROLE_USER"));
        List<TimedPosition> timedpostition=TimedPositionGenerator.get3();
        userArchiveRepository.save(new UserArchive("user1", "user1"+"_file"+UUID.randomUUID(), timedpostition));
        userArchiveRepository.save(new UserArchive("user2", "user2"+"_file"+UUID.randomUUID(), timedpostition));

        ArrayList<String> users=new ArrayList<String>();
		users.add("user1");
		users.add("user4");
		double coordinates[][][]={{{0,0},{0,70},{70,70},{70,0},{0,0}}};
		SearchResult res= positionService.getApproximatePositionInIntervalInPolygonInUserList(new Polygon(coordinates),new Date(),new Date(0),users);
		System.out.println("----------------Positions------------------");
		res.getByPosition().forEach(positionResult -> System.out.println(positionResult.toString()));
		System.out.println("----------------Timestamp------------------");
		res.getByTimestamp().forEach(timestampResult -> System.out.println(timestampResult.toString()));
		System.out.println("----------------User------------------");
		res.getByUser().forEach(userResult -> System.out.println(userResult.toString()));
        TimestampResult ts1=new TimestampResult("user1",0);
        TimestampResult ts2=new TimestampResult("user1",60);
        Assert.assertEquals(2, res.getByTimestamp().size());
        Assert.assertTrue(res.getByTimestamp().contains(ts1));
        Assert.assertTrue(res.getByTimestamp().contains(ts2));
        Assert.assertEquals(1, res.getByUser().size());
        Assert.assertTrue(res.getByUser().contains(new UserResult("user1")));
        Assert.assertEquals(1, res.getByPosition().size());
        double[] coord1={45.0,45.0};
        Assert.assertTrue(res.getByPosition().contains(new PositionResult("user1",new Point(coord1))));
    }
    @After
    public void multipleCleanup(){
            userRepository.deleteAll();
            userArchiveRepository.deleteAll();
    }

}
