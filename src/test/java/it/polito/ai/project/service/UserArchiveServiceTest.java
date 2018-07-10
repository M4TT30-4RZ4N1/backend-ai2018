package it.polito.ai.project.service;

import it.polito.ai.project.TimedPositionGenerator;
import it.polito.ai.project.security.RepositoryUserDetailsService;
import it.polito.ai.project.security.User;
import it.polito.ai.project.security.UserRepository;
import it.polito.ai.project.service.model.ClientInteraction.PositionResult;
import it.polito.ai.project.service.model.ClientInteraction.SearchResult;
import it.polito.ai.project.service.model.ClientInteraction.TimestampResult;
import it.polito.ai.project.service.model.ClientInteraction.UserResult;
import it.polito.ai.project.service.model.CustomerTransaction;
import it.polito.ai.project.service.model.TimedPosition;
import it.polito.ai.project.service.model.UserArchive;
import it.polito.ai.project.service.repositories.CustomerTransactionRepository;
import it.polito.ai.project.service.repositories.UserArchiveRepository;
import javassist.NotFoundException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.wololo.geojson.Point;
import org.wololo.geojson.Polygon;

import javax.servlet.ServletOutputStream;
import javax.validation.constraints.AssertTrue;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.*;
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserArchiveServiceTest {
    @Autowired
    UserArchiveService userArchiveService;
    @Autowired
    UserArchiveRepository userArchiveRepository;
    @Autowired
    CustomerTransactionService customerTransactionService;
    @Autowired
    CustomerTransactionRepository customerTransactionRepository;
    @Autowired
    RepositoryUserDetailsService userDetailsService;

    @Autowired
    UserRepository userRepository;
    @Before
    public void setUp() throws Exception {
        userRepository.deleteAll();
        userArchiveRepository.deleteAll();
    }

    @After
    public void tearDown() throws Exception {
        userRepository.deleteAll();
        userArchiveRepository.deleteAll();
    }

    @Test
    public void registrationValidation(){
        String email_OK = "antonio@test.it";
        String email_NOT_OK = "antonio.it";
        assertTrue(userDetailsService.validateEmail(email_OK) == true);
        assertTrue(userDetailsService.validateEmail(email_NOT_OK) == false);
        String user_OK = "Antonio94";
        String user_NOT_OK = "antonio@94";
        assertTrue(userDetailsService.validateUser(user_OK) == true);
        assertTrue(userDetailsService.validateUser(user_NOT_OK) == false);
    }

    @Test
    public void addArchive() {

        UserArchive archive=new UserArchive("user1","testfile",TimedPositionGenerator.get());
        userArchiveService.addArchive(archive);
        Assert.assertEquals(1, userArchiveRepository.count());
        UserArchive inserted=userArchiveRepository.findAll().get(0);
        Assert.assertEquals(inserted.getFilename(),archive.getFilename());
        Assert.assertEquals(inserted.getOwner(),archive.getOwner());
        Assert.assertEquals(inserted.getContent(),archive.getContent());
    }

    @Test
    public void addArchive1() {
        userArchiveService.addArchive("user1",TimedPositionGenerator.get());
        Assert.assertEquals(1, userArchiveRepository.count());
        UserArchive inserted=userArchiveRepository.findAll().get(0);
        Assert.assertEquals(inserted.getOwner(),"user1");
        Assert.assertEquals(TimedPositionGenerator.get(),inserted.getContent());
    }

    @Test
    public void getOwnArchivesWithoutContent() {
        userArchiveService.addArchive("user1",TimedPositionGenerator.get());
        Assert.assertTrue(userArchiveService.getOwnArchivesWithoutContent("user1").stream().allMatch(userArchive -> userArchive.getContent()==null));
    }

    @Test
    public void getPurchasedArchives() {
        customerTransactionRepository.deleteAll();
        UserArchive archive=new UserArchive("user1","testfile",TimedPositionGenerator.get());
        userArchiveService.addArchive(archive);
        customerTransactionService.addTransaction(new CustomerTransaction("user2","user1","testfile"));
        List<UserArchive> purchased=userArchiveService.getPurchasedArchives("user2");
        Assert.assertEquals(purchased.size(),1);
        Assert.assertEquals(purchased.get(0).getFilename(),archive.getFilename());
        Assert.assertEquals(purchased.get(0).getOwner(),archive.getOwner());
        Assert.assertNull(purchased.get(0).getContent()); //content must be empty
    }

    @Test
    public void createZip() {
        UserArchive archive1=new UserArchive("user1","testfile1",TimedPositionGenerator.get());
        userArchiveService.addArchive(archive1);
        UserArchive archive2=new UserArchive("user1","testfile2",TimedPositionGenerator.get2());
        userArchiveService.addArchive(archive2);
    }

    @Test
    public void deleteArchives() {
        UserArchive archive1=new UserArchive("user1","testfile1",TimedPositionGenerator.get());
        userArchiveService.addArchive(archive1);
        UserArchive archive2=new UserArchive("user1","testfile2",TimedPositionGenerator.get2());
        userArchiveService.addArchive(archive2);
        Assert.assertEquals(userArchiveRepository.findAll().size(),2);
        ArrayList<String> files=new ArrayList<String>();
        files.add("testfile1");
        files.add("testfile2");
        userArchiveService.deleteArchives("user1",files);
        Assert.assertEquals(userArchiveRepository.findAll().size(),2);
        Assert.assertTrue(userArchiveRepository.findAll().stream().allMatch(UserArchive::isDeleted));
    }
    @Test(expected = AccessDeniedException.class)
    public void deleteNotMyArchives() {
        UserArchive archive1=new UserArchive("user2","testfile1",TimedPositionGenerator.get());
        userArchiveService.addArchive(archive1);
        UserArchive archive2=new UserArchive("user2","testfile2",TimedPositionGenerator.get2());
        userArchiveService.addArchive(archive2);
        Assert.assertEquals(userArchiveRepository.findAll().size(),2);
        ArrayList<String> files=new ArrayList<String>();
        files.add("testfile1");
        files.add("testfile2");
        userArchiveService.deleteArchives("user1",files);
    }
    @Test
    public void deleteArchive() {
        UserArchive archive=new UserArchive("user1","testfile",TimedPositionGenerator.get());
        userArchiveService.addArchive(archive);
        Assert.assertEquals(userArchiveRepository.findAll().size(),1);
        userArchiveService.deleteArchive("user1","testfile");
        Assert.assertEquals(userArchiveRepository.findAll().size(),1);
        Assert.assertTrue(userArchiveRepository.findAll().stream().allMatch(UserArchive::isDeleted));
    }

    @Test
    public void downloadArchive() throws NotFoundException {
        UserArchive archive=new UserArchive("user1","testfile",TimedPositionGenerator.get());
        userArchiveService.addArchive(archive);
        List<TimedPosition> inserted=userArchiveService.downloadArchive("user1","testfile");
        Assert.assertEquals(inserted,archive.getContent());
    }
    @Test(expected = AccessDeniedException.class)
    public void downloadNotAuthArchive() throws NotFoundException {
        UserArchive archive=new UserArchive("user2","testfile",TimedPositionGenerator.get());
        userArchiveService.addArchive(archive);
        List<TimedPosition> inserted=userArchiveService.downloadArchive("user1","testfile");
    }
    @Test
    public void downloadPurchasedArchive() throws NotFoundException {
        customerTransactionRepository.deleteAll();
        UserArchive archive=new UserArchive("user2","testfile",TimedPositionGenerator.get());
        userArchiveService.addArchive(archive);
        CustomerTransaction testTransaction1 = new CustomerTransaction("user1","user2","testfile");
        customerTransactionService.addTransaction(testTransaction1);
        List<TimedPosition> inserted=userArchiveService.downloadArchive("user1","testfile");
        Assert.assertEquals(TimedPositionGenerator.get(),inserted);
        CustomerTransaction transaction1 = customerTransactionRepository.findAll().get(0);
        Assert.assertTrue(transaction1.equals(testTransaction1));
        customerTransactionRepository.deleteAll();
    }

    @Test
    public void validatePositions(){
        UserArchive archive=new UserArchive("user2","testfile",TimedPositionGenerator.get());
        userArchiveService.addArchive(archive);
        UserArchive archive2=new UserArchive("user2","testfile2",TimedPositionGenerator.get2());
        userArchiveService.addArchive(archive2);
        UserArchive archive3=new UserArchive("user2","testfile3",TimedPositionGenerator.get());
        userArchiveService.addArchive(archive3);
        List<UserArchive> userArchives = userArchiveRepository.findAll();
        Assert.assertEquals(2, userArchives.size());
        List<TimedPosition> contents = userArchives.stream().flatMap(a -> a.getContent().stream()).collect(Collectors.toList());
        Assert.assertEquals(8, contents.size());
    }
    @Test
    public  void TestPositionLatLong(){
        List<TimedPosition> timedPositions = new ArrayList<TimedPosition>();
        timedPositions.add(new TimedPosition(89,170,0));
        timedPositions.add(new TimedPosition(89,-170,100000));
        timedPositions.add(new TimedPosition(-89,-170,200000));
        timedPositions.add(new TimedPosition(-89,+170,300000));
        userArchiveRepository.save(new UserArchive("user1", "user1"+"_file"+UUID.randomUUID(), timedPositions));
        List<UserArchive> userArchives = userArchiveRepository.findAll();
        System.out.println(userArchives.get(0).getContent());
        Assert.assertEquals(1, userArchives.size());
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
        users.add("user4");/*
                timedpostition.add(new TimedPosition(45.00002, 50.00, new Date(0).getTime()));
        timedpostition.add(new TimedPosition(45.00001, 50.00, new Date(59).getTime()));
        timedpostition.add(new TimedPosition(44.99999, 50.00, new Date(99).getTime()));
        timedpostition.add(new TimedPosition(44.99998, 50.00, new Date(119).getTime()));*/
        double coordinates[][][]={{{170.0001,44.99},{170.0001,45.01},{169.9999,45.01},{169.9999,44.99},{170.0001,44.99}}};
        SearchResult res= userArchiveService.getApproximatePositionInIntervalInPolygonInUserList(new Polygon(coordinates),new Date(0),new Date(),users);
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
        double[] coord1={170.0,45.0};
        Assert.assertTrue(res.getByPosition().contains(new PositionResult("user1",new Point(coord1))));
    }
}