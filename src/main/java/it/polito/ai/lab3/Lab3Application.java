package it.polito.ai.lab3;

import it.polito.ai.lab3.security.User;
import it.polito.ai.lab3.security.UserRepository;
import it.polito.ai.lab3.service.model.CustomerTransaction;
import it.polito.ai.lab3.service.model.TimedPosition;
import it.polito.ai.lab3.service.repositories.CustomerTransactionRepository;
import it.polito.ai.lab3.service.repositories.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Date;

@SpringBootApplication
public class Lab3Application {
	@Autowired
    PositionRepository positionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CustomerTransactionRepository transactionRepository;

	public static void main(String[] args) {
		SpringApplication.run(Lab3Application.class, args);
	}
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }
        };
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
