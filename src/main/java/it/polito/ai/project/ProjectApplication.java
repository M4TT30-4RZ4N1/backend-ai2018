package it.polito.ai.project;

import it.polito.ai.project.security.User;
import it.polito.ai.project.security.UserRepository;
import it.polito.ai.project.service.model.CustomerTransaction;
import it.polito.ai.project.service.model.TimedPosition;
import it.polito.ai.project.service.model.UserArchive;
import it.polito.ai.project.service.repositories.CustomerTransactionRepository;
import it.polito.ai.project.service.repositories.PositionRepository;
import it.polito.ai.project.service.repositories.UserArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class ProjectApplication {
	@Autowired
    PositionRepository positionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CustomerTransactionRepository transactionRepository;
    @Autowired
    UserArchiveRepository userArchiveRepository;
	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
	}

    @Bean
    public CommandLineRunner initDB(){
        return args -> {
            System.out.println("Running initialization");
            userRepository.deleteAll();
            userRepository.save(new User("testuser","testpassword","ROLE_USER"));
            userRepository.save(new User("testadmin","testpassword","ROLE_ADMIN"));
            userRepository.save(new User("testcustomer","testpassword","ROLE_CUSTOMER"));
            userRepository.save(new User("user1","testpassword","ROLE_USER"));
            userRepository.save(new User("user2","testpassword","ROLE_USER"));
            userRepository.save(new User("user3","testpassword","ROLE_USER"));
            positionRepository.deleteAll();

            // save a couple of customers
            //positionRepository.save(new TimedPosition(45.00, 47.00, new Date().getTime()));
            //positionRepository.save(new TimedPosition(55.00, 47.00, new Date().getTime(), "testuser"));
            //positionRepository.save(new TimedPosition(45.20, 47.00, new Date().getTime(), "testuser"));
            //positionRepository.save(new TimedPosition(57.00, 47.00, new Date().getTime(), "testuser"));
            userArchiveRepository.deleteAll();
            userRepository.findAll().stream().filter(user -> user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))).forEach(user -> {
                int j = 0;
                ArrayList<TimedPosition> timedpostition=new ArrayList<TimedPosition>();
                for (int i = 0; i < 20; i++) {
                    timedpostition.add(new TimedPosition(45.00 + Math.random()/10, 45.00, new Date().getTime()));
                }
                userArchiveRepository.save(new UserArchive(user.getUsername(), user.getUsername()+"_file"+j, timedpostition));
                j++;
            });


            // fetch all customers
            System.out.println("Positions found with findAll():");
            System.out.println("-------------------------------");
            for (TimedPosition position : positionRepository.findAll()) {
                System.out.println(position);
            }
            System.out.println();
            System.out.println("Transactions found with findAll():");
            System.out.println("-------------------------------");
            for (CustomerTransaction transaction : transactionRepository.findAll()) {
                System.out.println(transaction);
            }
            System.out.println();
        };
    }
}
