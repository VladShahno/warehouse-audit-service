package warehouse.com.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static warehouse.com.utils.TestUtils.CREATED_ENTITY_ACTION;
import static warehouse.com.utils.TestUtils.SYSTEM_IMAGE_ENTITY_TYPE;
import static warehouse.com.utils.TestUtils.mockEvents;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import warehouse.com.auditservice.Application;
import warehouse.com.auditservice.model.ActivityEvent;
import warehouse.com.auditservice.model.dto.ActivityEventDto;
import warehouse.com.auditservice.model.dto.ActivityEventRequest;
import warehouse.com.auditservice.model.dto.BodyActivityEventRequest;
import warehouse.com.auditservice.repository.ActivityEventRepository;
import warehouse.com.auditservice.service.ActivityEventService;
import warehouse.com.config.MongoTestContainerConfiguration;
import warehouse.com.csv.service.CsvService;
import warehouse.com.eventstarter.model.AuditEvent;

@SpringBootTest(classes = Application.class)
@Import(MongoTestContainerConfiguration.class)
@EnableAutoConfiguration(exclude = {
    SecurityAutoConfiguration.class})
class ActivityEventServiceTest {

  @Autowired
  private ActivityEventService activityEventService;

  @Autowired
  private ActivityEventRepository activityEventRepository;

  @MockBean
  private CsvService csvService;

  private static Stream<Arguments> argsForFilterTest() {
    return Stream.of(
        Arguments.of("product", 5, Set.of("prdct"), 0),
        Arguments.of("product", 5, Set.of("product"), 5),
        Arguments.of("product", 5, null, 105) //100 other events are created inside a test
    );
  }

  @AfterEach
  void init() {
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
        .description("desc").build();    //when
    activityEventService.saveEventEntity(ActivityEvent.createActivityEvents(auditEvent));
    //then
    ActivityEvent activityEvent = activityEventRepository.findAll().get(0);
    ActivityEvent expectedActivity = ActivityEvent.createActivityEvents(auditEvent).get(0);
    assertThat(activityEvent)
        .isEqualToIgnoringGivenFields(expectedActivity, "activityEventId");
    assertThat(activityEventRepository.findAll().get(0))
        .isEqualToIgnoringGivenFields(ActivityEvent.createActivityEvents(auditEvent).get(0),
            "activityEventId");
  }

  @ParameterizedTest
  @MethodSource("argsForFilterTest")
  void shouldFilterEventsForCsvByCategories(
      String existingType,
      int count,
      Set<String> searchForType,
      int expectedCount) {
    var events = mockEvents(existingType, "updated", count);
    var otherEvents = mockEvents(SYSTEM_IMAGE_ENTITY_TYPE, "updated", 100);
    activityEventRepository.saveAll(events);
    activityEventRepository.saveAll(otherEvents);

    var bodyActivityEventRequest = new BodyActivityEventRequest();
    bodyActivityEventRequest.setIds(List.of());
    var activityEventRequest = new ActivityEventRequest();
    activityEventRequest.setEntityType(searchForType);
    var listCaptor = ArgumentCaptor.forClass(List.class);

    //when
    activityEventService.exportCsv(Writer.nullWriter(), activityEventRequest,
        bodyActivityEventRequest, Sort.unsorted());

    verify(csvService).exportDataToWriter(any(), listCaptor.capture(), any(), any());

    assertThat(listCaptor.getValue()).hasSize(expectedCount);
  }

  private ActivityEventRequest createActivityEventRequest(Set<String> entityTypeSet) {
    var activityEventRequest = new ActivityEventRequest();
    activityEventRequest.setEntityType(entityTypeSet);
    return activityEventRequest;
  }

  private List<ActivityEvent> createActivityEvents() {
    var testEntitiesList = new ArrayList<ActivityEvent>();
    testEntitiesList.addAll(
        mockEvents("Product", CREATED_ENTITY_ACTION, 5));
    testEntitiesList.addAll(
        mockEvents("Warehouse", CREATED_ENTITY_ACTION, 4));
    testEntitiesList.addAll(
        mockEvents("Product Group", CREATED_ENTITY_ACTION, 3));
    return testEntitiesList;
  }

  private List<ActivityEventDto> filterResult(
      Page<ActivityEventDto> result,
      String entityType) {
    return result.stream()
        .filter(activityEventDto -> activityEventDto.getEntityType().equals(entityType))
        .toList();
  }
}
