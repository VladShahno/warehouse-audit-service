package warehouse.com;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.test.context.ActiveProfiles;
import warehouse.com.auditservice.Application;
import warehouse.com.config.MongoTestContainerConfiguration;

@SpringBootTest(classes = Application.class)
@Import(MongoTestContainerConfiguration.class)
class ApplicationTest {

  @MockBean
  private KafkaAdmin kafkaAdmin;

  @Test
  void contextLoads() {
  }
}
