package warehouse.com.auditservice.configuration;

import java.util.concurrent.TimeUnit;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

  @Bean
  public MongoClientSettingsBuilderCustomizer customizeMongoClient() {
    return clientSettingsBuilder ->
        clientSettingsBuilder.applyToConnectionPoolSettings(builder ->
            builder
                .maxConnectionIdleTime(2, TimeUnit.MINUTES)
                .maxConnectionLifeTime(3, TimeUnit.MINUTES));
  }
}
