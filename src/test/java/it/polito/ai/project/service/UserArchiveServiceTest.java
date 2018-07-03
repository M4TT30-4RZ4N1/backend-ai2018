package it.polito.ai.project.service;

import it.polito.ai.project.TimedPositionGenerator;
import it.polito.ai.project.service.model.CustomerTransaction;
import it.polito.ai.project.service.model.TimedPosition;
import it.polito.ai.project.service.model.UserArchive;
import it.polito.ai.project.service.repositories.CustomerTransactionRepository;
import it.polito.ai.project.service.repositories.UserArchiveRepository;
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

import javax.servlet.ServletOutputStream;
import javax.validation.constraints.AssertTrue;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    @Before
    public void setUp() throws Exception {
        userArchiveRepository.deleteAll();
    }

    @After
    public void tearDown() throws Exception {
        userArchiveRepository.deleteAll();
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
    public void downloadArchive() {
        UserArchive archive=new UserArchive("user1","testfile",TimedPositionGenerator.get());
        userArchiveService.addArchive(archive);
        List<TimedPosition> inserted=userArchiveService.downloadArchive("user1","testfile");
        Assert.assertEquals(inserted,archive.getContent());
    }
    @Test(expected = AccessDeniedException.class)
    public void downloadNotAuthArchive() {
        UserArchive archive=new UserArchive("user2","testfile",TimedPositionGenerator.get());
        userArchiveService.addArchive(archive);
        List<TimedPosition> inserted=userArchiveService.downloadArchive("user1","testfile");
    }
    @Test
    public void downloadPurchasedArchive() {
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
}