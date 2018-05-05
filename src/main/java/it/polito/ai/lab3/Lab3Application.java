package it.polito.ai.lab3;

import it.polito.ai.lab3.service.Repositories.PositionRepository;
import it.polito.ai.lab3.service.model.TimedPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
public class Lab3Application {
	@Autowired
	PositionRepository positionRepository;

	public static void main(String[] args) {
		SpringApplication.run(Lab3Application.class, args);
	}

    @Bean
    public CommandLineRunner initDB(){
        return args -> {
            System.out.println("Running initialization");
            positionRepository.deleteAll();

            // save a couple of customers
            positionRepository.save(new TimedPosition(45.00, 47.00, new Date().getTime()));

            // fetch all customers
            System.out.println("Customers found with findAll():");
            System.out.println("-------------------------------");
            for (TimedPosition postion : positionRepository.findAll()) {
                System.out.println(postion);
            }
            System.out.println();
        };
    }
}
