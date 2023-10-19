package warehouse.com.utils;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import warehouse.com.auditservice.model.ActivityEvent;

public class TestUtils {

  public static final String CREATED_ENTITY_ACTION = "Created";
  public static final String SYSTEM_IMAGE_ENTITY_TYPE = "system-image";

  public static List<ActivityEvent> mockEvents(String type, String action, int count) {
    return IntStream.range(0, count).mapToObj(index -> mockEvent(type, action, index))
        .collect(Collectors.toList());

  }

  public static ActivityEvent mockEvent(String type, String action, int index) {
    return ActivityEvent.builder()
        .entityType(type)
        .entityId(UUID.randomUUID().toString())
        .entityName(type + index)
        .action(action)
        .initiatorId("Vlad")
        .timestamp(new Date())
        .build();
  }
}
