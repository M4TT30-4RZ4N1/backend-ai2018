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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerServiceTest {
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
    public void downloadPurchasedArchive() {
        customerTransactionRepository.deleteAll();
        UserArchive archive=new UserArchive("user2","testfile",TimedPositionGenerator.get());
        userArchiveService.addArchive(archive);
        UserArchive archive2=new UserArchive("user2","testfile2",TimedPositionGenerator.get2());
        userArchiveService.addArchive(archive2);
        UserArchive archive3=new UserArchive("user2","testfile3",TimedPositionGenerator.get());
        userArchiveService.addArchive(archive3);
        CustomerTransaction testTransaction1 = new CustomerTransaction("user1","user2","testfile");
        customerTransactionService.addTransaction(testTransaction1);
        CustomerTransaction transaction1 = customerTransactionRepository.findAll().get(0);
        Assert.assertTrue(transaction1.equals(testTransaction1));
        customerTransactionRepository.deleteAll();
    }

}