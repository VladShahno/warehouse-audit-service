package warehouse.com.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import warehouse.com.auditservice.Application;
import warehouse.com.auditservice.model.ActivityEvent;
import warehouse.com.auditservice.model.dto.ActivityEventRequest;
import warehouse.com.auditservice.model.dto.BodyActivityEventRequest;
import warehouse.com.auditservice.repository.ActivityEventRepository;
import warehouse.com.config.MongoTestContainerConfiguration;

@SpringBootTest(classes = Application.class)
@Import(MongoTestContainerConfiguration.class)
@EnableAutoConfiguration(exclude = {
    SecurityAutoConfiguration.class})
class ActivityEventCustomRepositoryTest {

  @Autowired
  private ActivityEventRepository repository;

  @AfterEach
  void tearDown() {
    repository.deleteAll();
  }

  @Test
  void shouldFindAllActivityEvents() {
    var activityEventRequest = new ActivityEventRequest();
    String entityType = "device";

    var activityEvent1 = stubActivityEvent(entityType);
    var activityEvent2 = stubActivityEvent(entityType);
    var activityEvent3 = stubActivityEvent(entityType);
    var activityEvent4 = stubActivityEvent(entityType);

    repository.saveAll(List.of(activityEvent1, activityEvent2, activityEvent3, activityEvent4));

    var activityEvents =
        repository.findAll(activityEventRequest, Set.of(entityType), Pageable.unpaged());

    assertEquals(4, activityEvents.getSize());
  }

  @Test
  void shouldFindAllActivityEventsByEntityId() {
    String entityId = UUID.randomUUID().toString();
    var activityEventRequest = new ActivityEventRequest();
    activityEventRequest.setEntityId(entityId);

    String entityType = "device";

    var activityEvent1 = stubActivityEvent(entityType);
    activityEvent1.setEntityId(entityId);
    var activityEvent2 = stubActivityEvent(entityType);
    activityEvent2.setEntityId("1234-5678");
    var activityEvent3 = stubActivityEvent(entityType);
    activityEvent3.setEntityId(entityId);

    repository.saveAll(List.of(activityEvent1, activityEvent2, activityEvent3));

    var activityEvents =
        repository.findAll(activityEventRequest, Set.of(entityType), Pageable.unpaged());

    assertEquals(2, activityEvents.getSize());
  }

  @Test
  void shouldFindAllActivityEventsByFromAndTo() {
    var activityEventRequest = new ActivityEventRequest();
    activityEventRequest.setFrom(new GregorianCalendar(2022, Calendar.JANUARY, 1).getTime());
    activityEventRequest.setTo(new GregorianCalendar(2022, Calendar.FEBRUARY, 1).getTime());

    String entityType = "device";

    var activityEvent1 = stubActivityEvent(entityType);
    activityEvent1.setTimestamp(new GregorianCalendar(2021, Calendar.DECEMBER, 31).getTime());
    var activityEvent2 = stubActivityEvent(entityType);
    activityEvent2.setTimestamp(new GregorianCalendar(2022, Calendar.JANUARY, 7).getTime());
    var activityEvent3 = stubActivityEvent(entityType);
    activityEvent3.setTimestamp(new GregorianCalendar(2022, Calendar.FEBRUARY, 14).getTime());

    repository.saveAll(List.of(activityEvent1, activityEvent2, activityEvent3));

    var activityEvents =
        repository.findAll(activityEventRequest, Set.of(entityType), Pageable.unpaged());

    assertEquals(1, activityEvents.getSize());
  }

  @Test
  void shouldFindAllActivityEventsByActionSearch() {
    var activityEventRequest = new ActivityEventRequest();
    activityEventRequest.setActionSearch("*created");

    String entityType = "device";

    var activityEvent1 = stubActivityEvent(entityType);
    activityEvent1.setAction("Created");
    var activityEvent2 = stubActivityEvent(entityType);
    activityEvent2.setAction("updated");
    var activityEvent3 = stubActivityEvent(entityType);
    activityEvent3.setAction("Device was created");

    repository.saveAll(List.of(activityEvent1, activityEvent2, activityEvent3));

    var activityEvents =
        repository.findAll(activityEventRequest, Set.of(entityType), Pageable.unpaged());

    assertEquals(2, activityEvents.getSize());
  }

  @Test
  void shouldFindAllActivityEventsByActionSearchWithWhiteSpace() {
    var activityEventRequest = new ActivityEventRequest();
    activityEventRequest.setActionSearch("icon uploaded");

    String entityType = "application";

    var activityEvent1 = stubActivityEvent(entityType);
    activityEvent1.setAction("Icon uploaded");
    var activityEvent2 = stubActivityEvent(entityType);
    activityEvent2.setAction("Icon scanned - clean");
    var activityEvent3 = stubActivityEvent(entityType);
    activityEvent3.setAction("Icon uploaded once again");

    repository.saveAll(List.of(activityEvent1, activityEvent2, activityEvent3));

    var activityEvents =
        repository.findAll(activityEventRequest, Set.of(entityType), Pageable.unpaged());

    assertEquals(2, activityEvents.getSize());
  }

  @Test
  void shouldFindAllActivityEventsForCsv() {
    var activityEventRequest = new ActivityEventRequest();
    var bodyActivityEventRequest = new BodyActivityEventRequest();
    int csvLimit = 3;

    String entityType = "device";

    var activityEvent1 = stubActivityEvent(entityType);
    var activityEvent11 = stubActivityEvent(entityType);
    var activityEvent111 = stubActivityEvent(entityType);
    var activityEvent1111 = stubActivityEvent(entityType);
    var activityEvent11111 = stubActivityEvent(entityType);
    var activityEvent2 = stubActivityEvent(entityType);
    var activityEvent3 = stubActivityEvent("order");
    var activityEvent4 = stubActivityEvent(entityType);

    repository.saveAll(List.of(activityEvent1, activityEvent11, activityEvent111, activityEvent1111,
        activityEvent11111,
        activityEvent2, activityEvent3, activityEvent4));

    var activityEvents =
        repository.findAllForCsv(activityEventRequest, bodyActivityEventRequest,
            Set.of(entityType), csvLimit, Sort.unsorted());

    assertEquals(3, activityEvents.size());
  }

  @Test
  void shouldFindAllActivityEventsForCsvByEntityIds() {
    String entityId = UUID.randomUUID().toString();
    var activityEventRequest = new ActivityEventRequest();
    var bodyActivityEventRequest = new BodyActivityEventRequest();
    bodyActivityEventRequest.setIds(List.of(entityId));

    String entityType = "device";

    var activityEvent1 = stubActivityEvent(entityType);
    activityEvent1.setEntityId(entityId);
    var activityEvent2 = stubActivityEvent(entityType);
    activityEvent2.setEntityId("1234");
    var activityEvent3 = stubActivityEvent(entityType);
    activityEvent3.setEntityId(entityId);
    var activityEvent4 = stubActivityEvent(entityType);
    activityEvent4.setEntityId("5678");
    var activityEvent5 = stubActivityEvent(entityType);
    activityEvent5.setEntityId(entityId);

    repository.saveAll(
        List.of(activityEvent1, activityEvent2, activityEvent3, activityEvent4, activityEvent5));

    var activityEvents =
        repository.findAllForCsv(activityEventRequest, bodyActivityEventRequest,
            Set.of(entityType), 100, Sort.unsorted());

    assertEquals(3, activityEvents.size());
  }

  @Test
  void shouldFindAllActivityEventsForCsvByActivityEventIds() {
    String entityId = UUID.randomUUID().toString();
    var activityEventRequest = new ActivityEventRequest();

    String entityType = "device";

    var activityEvent1 = stubActivityEvent(entityType);
    activityEvent1.setEntityId(entityId);
    var activityEvent2 = stubActivityEvent(entityType);
    activityEvent2.setEntityId("1234");
    var activityEvent3 = stubActivityEvent(entityType);
    activityEvent3.setEntityId(entityId);
    var activityEvent4 = stubActivityEvent(entityType);
    activityEvent4.setEntityId("5678");
    var activityEvent5 = stubActivityEvent(entityType);
    activityEvent5.setEntityId(entityId);

    var bodyActivityEventRequest = new BodyActivityEventRequest();
    bodyActivityEventRequest.setActivityEventIds(
        List.of(activityEvent1.getActivityEventId(), activityEvent3.getActivityEventId()));

    repository.saveAll(
        List.of(activityEvent1, activityEvent2, activityEvent3, activityEvent4, activityEvent5));

    var activityEvents =
        repository.findAllForCsv(activityEventRequest, bodyActivityEventRequest,
            Set.of(entityType), 100, Sort.unsorted());

    assertEquals(2, activityEvents.size());
  }

  @Test
  void shouldFindAllActivityEventsForCsvSorted() {
    var activityEventRequest = new ActivityEventRequest();
    var bodyActivityEventRequest = new BodyActivityEventRequest();

    String entityType = "device";

    var activityEvent1 = stubActivityEvent(entityType);
    activityEvent1.setEntityId("abcd");
    var activityEvent2 = stubActivityEvent(entityType);
    activityEvent2.setEntityId("1234");
    var activityEvent3 = stubActivityEvent(entityType);
    activityEvent3.setEntityId("efgh");
    var activityEvent4 = stubActivityEvent(entityType);
    activityEvent4.setEntityId("5678");
    var activityEvent5 = stubActivityEvent(entityType);
    activityEvent5.setEntityId("ijkl");

    repository.saveAll(
        List.of(activityEvent1, activityEvent2, activityEvent3, activityEvent4, activityEvent5));

    var sort = Sort.by(Sort.Direction.ASC, "entityId");
    var activityEvents =
        repository.findAllForCsv(activityEventRequest, bodyActivityEventRequest,
            Set.of(entityType), 100, sort);

    var expectedActivityEvents =
        List.of(activityEvent2, activityEvent4, activityEvent1, activityEvent3, activityEvent5);

    assertEquals(expectedActivityEvents, activityEvents);
  }

  private ActivityEvent stubActivityEvent(String entityType) {
    return ActivityEvent.builder()
        .activityEventId(UUID.randomUUID().toString())
        .entityType(entityType)
        .build();
  }
}
