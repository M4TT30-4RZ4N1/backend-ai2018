package it.polito.ai.project.configuration;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

//@Configuration
//@EnableMongoRepositories("it.polito.ai.project.service.repositories")
public class ConfigurationRep {
    //different from the slide, Mongo -> MongoClient
    /*public @Bean
    MongoClient mongo() throws Exception{
        return new MongoClient("159.122.181.42:31287");
    }
    public @Bean
    MongoTemplate mongoTemplate() throws Exception{
        return new MongoTemplate(mongo(), "db");
    }*/
}
