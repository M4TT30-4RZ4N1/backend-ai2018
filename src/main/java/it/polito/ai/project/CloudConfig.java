package it.polito.ai.project;

import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;

@Configuration
@Profile({ "cloud" })
public class CloudConfig extends AbstractCloudConfig {
  @Bean
  public MongoDbFactory documentMongoDbFactory() {
      System.out.println(" CloudConfiguration : using connectionFactory to set data source");
      return connectionFactory().mongoDbFactory();
  }
}