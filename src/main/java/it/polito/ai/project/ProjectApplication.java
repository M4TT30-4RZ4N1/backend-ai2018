package it.polito.ai.project;

import it.polito.ai.project.security.User;
import it.polito.ai.project.security.UserRepository;
import it.polito.ai.project.service.model.CustomerTransaction;
import it.polito.ai.project.service.model.TimedPosition;
import it.polito.ai.project.service.model.UserArchive;
import it.polito.ai.project.service.repositories.CustomerTransactionRepository;
import it.polito.ai.project.service.repositories.UserArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

@SpringBootApplication
public class ProjectApplication {
    private final
    UserRepository userRepository;
    private final
    CustomerTransactionRepository transactionRepository;
    private final
    UserArchiveRepository userArchiveRepository;

    @Autowired
    public ProjectApplication(UserRepository userRepository, CustomerTransactionRepository transactionRepository, UserArchiveRepository userArchiveRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.userArchiveRepository = userArchiveRepository;
    }

    public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
	}

    @Profile({ "dev" })
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
            userRepository.save(new User("user4","testpassword","ROLE_USER"));

            // save a couple of customers
            //positionRepository.save(new TimedPosition(45.00, 47.00, new Date().getTime()));
            //positionRepository.save(new TimedPosition(55.00, 47.00, new Date().getTime(), "testuser"));
            //positionRepository.save(new TimedPosition(45.20, 47.00, new Date().getTime(), "testuser"));
            //positionRepository.save(new TimedPosition(57.00, 47.00, new Date().getTime(), "testuser"));
            userArchiveRepository.deleteAll();
            userRepository.findAll().stream().filter(user -> user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))).forEach(user -> {
                System.out.println("Adding test archive for "+ user.getUsername());
                ArrayList<TimedPosition> timedpostition=new ArrayList<TimedPosition>();
                for (int i = 0; i < 20; i++) {
                    timedpostition.add(new TimedPosition(45.00 + Math.random()/10, 45.00, new Date().getTime()+i));
                }
                userArchiveRepository.save(new UserArchive(user.getUsername(), user.getUsername()+"_"+(new Date().getTime())+"_"+UUID.randomUUID().toString().replace("-", "")+".json", timedpostition));
                userArchiveRepository.save(new UserArchive(user.getUsername(), user.getUsername()+"_"+(new Date().getTime())+"_"+UUID.randomUUID().toString().replace("-", "")+".json", timedpostition));
                userArchiveRepository.save(new UserArchive(user.getUsername(), user.getUsername()+"_"+(new Date().getTime())+"_"+UUID.randomUUID().toString().replace("-", "")+".json", timedpostition));
            });


            // fetch all customers
            System.out.println("Archive found with findAll():");
            System.out.println("-------------------------------");
            for (UserArchive archive : userArchiveRepository.findAll()) {
                System.out.println(archive);
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
