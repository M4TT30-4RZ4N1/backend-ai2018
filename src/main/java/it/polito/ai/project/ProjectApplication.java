package it.polito.ai.project;

import it.polito.ai.project.security.User;
import it.polito.ai.project.security.UserRepository;
import it.polito.ai.project.service.model.CustomerTransaction;
import it.polito.ai.project.service.model.TimedPosition;
import it.polito.ai.project.service.repositories.CustomerTransactionRepository;
import it.polito.ai.project.service.repositories.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
public class ProjectApplication {
	@Autowired
    PositionRepository positionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CustomerTransactionRepository transactionRepository;

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
            positionRepository.deleteAll();

            // save a couple of customers
            //positionRepository.save(new TimedPosition(45.00, 47.00, new Date().getTime()));
            positionRepository.save(new TimedPosition(55.00, 47.00, new Date().getTime(), "testuser"));
            positionRepository.save(new TimedPosition(45.20, 47.00, new Date().getTime(), "testuser"));
            positionRepository.save(new TimedPosition(57.00, 47.00, new Date().getTime(), "testuser"));



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
