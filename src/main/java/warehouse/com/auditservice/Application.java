package warehouse.com.auditservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import warehouse.com.auditservice.configuration.CsvProperties;

@SpringBootApplication
@ComponentScan(basePackages = {"warehouse.com"})
@EnableConfigurationProperties({CsvProperties.class})
@EnableMongoRepositories
@EnableKafka
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);

  }
}
