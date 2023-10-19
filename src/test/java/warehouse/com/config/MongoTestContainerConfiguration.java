package warehouse.com.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class MongoTestContainerConfiguration {

  private static final String MONGO_DB_FULL_IMAGE_NAME = "mongo:5.0.7";
  private static final String MONGO_DB_OTHER_IMAGE_NAME = "mongo";
  private static final String URI_PROPERTY = "spring.data.mongodb.uri";

  private static final MongoDBContainer MONGO_DB_CONTAINER = new MongoDBContainer(
      DockerImageName.parse(MONGO_DB_FULL_IMAGE_NAME)
          .asCompatibleSubstituteFor(MONGO_DB_OTHER_IMAGE_NAME));

  static {
    MONGO_DB_CONTAINER.start();
    System.setProperty(URI_PROPERTY, MONGO_DB_CONTAINER.getReplicaSetUrl());
  }
}
