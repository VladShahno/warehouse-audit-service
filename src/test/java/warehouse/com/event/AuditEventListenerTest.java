package warehouse.com.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaAdmin;
import warehouse.com.auditservice.Application;
import warehouse.com.auditservice.event.AuditEventListener;
import warehouse.com.auditservice.model.ActivityEvent;
import warehouse.com.auditservice.repository.ActivityEventRepository;
import warehouse.com.config.MongoTestContainerConfiguration;
import warehouse.com.eventstarter.model.AuditEvent;

@SpringBootTest(classes = Application.class)
@Import(MongoTestContainerConfiguration.class)
@EnableAutoConfiguration(exclude = {
    SecurityAutoConfiguration.class})
class AuditEventListenerTest {

  @Autowired
  private AuditEventListener auditEventListener;

  @Autowired
  private ActivityEventRepository activityEventRepository;

  @MockBean
  private KafkaAdmin kafkaAdmin;

  @AfterEach
  public void init() {
    activityEventRepository.deleteAll();
  }

  @Test
  void shouldSaveAuditEventToDb() {
    //given
    AuditEvent.Entity entity = new AuditEvent.Entity();
    entity.setId("1");
    entity.setName("asd");
    var auditEvent = AuditEvent.builder()
        .entityType("type")
        .entities(Set.of(entity))
        .initiatorId("inId")
        .action("action")
        .timestamp(new Date())
        .description("desc").build();
    //when
    auditEventListener.handleActivityEvent(auditEvent);
    //then
    ActivityEvent activityEvent = activityEventRepository.findAll().get(0);
    ActivityEvent expectedActivity = ActivityEvent.createActivityEvents(auditEvent).get(0);
    assertThat(activityEvent)
        .isEqualToIgnoringGivenFields(expectedActivity, "activityEventId");
  }
}
