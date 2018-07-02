package it.polito.ai.project.security;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class RepositoryUserDetailsServiceTest {
    @Autowired
    RepositoryUserDetailsService repositoryUserDetailsService;
    @Autowired
    UserRepository userRepository;
    @Before
    public void setUp() throws Exception {
        userRepository.deleteAll();
    }

    @After
    public void tearDown() throws Exception {
        userRepository.deleteAll();
    }
    @Test
    public void addUser() {
        repositoryUserDetailsService.addUser("pippo@example.com","pippo","pluto");
        Assert.assertEquals(1, userRepository.findAll().size());
        Assert.assertEquals("pippo", userRepository.findAll().get(0).getUsername());
        Assert.assertEquals("pippo@example.com", userRepository.findAll().get(0).getEmail());
    }
    @Test(expected = DuplicateKeyException.class)
    public void addUserDuplicate() {
        repositoryUserDetailsService.addUser("pippo@example.com","pippo","pluto");
        repositoryUserDetailsService.addUser("pippo@example.com","pippo","pluto");
    }
    @Test
    public void loadUserByUsername() {
        repositoryUserDetailsService.addUser("pippo@example.com","pippo","pluto");
        Assert.assertNotNull(repositoryUserDetailsService.loadUserByUsername("pippo"));
    }
    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsernameNotFound() {
        repositoryUserDetailsService.loadUserByUsername("pippo");
    }
}