package it.polito.ai.project;

import it.polito.ai.project.service.PositionService;
import it.polito.ai.project.service.model.ClientInteraction.SearchResult;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.wololo.geojson.Polygon;

import java.util.ArrayList;
import java.util.Date;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class Lab3ApplicationTests {

	@Test
	public void contextLoads() {
        Assert.assertTrue(true);
	}


}
