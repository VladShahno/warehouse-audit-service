package warehouse.com.auditservice.model;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import warehouse.com.eventstarter.model.AuditEvent;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "activity-events")
@CompoundIndex(def = "{'entityType': 1, 'timestamp': -1}")
public class ActivityEvent {

  @Id
  private String activityEventId;
  private String initiatorId;
  private String action;
  private String description;
  private Date timestamp;
  @Indexed
  private String entityId;
  private String entityType;
  private String entityName;

  private ActivityEvent(String entityId, String entityName) {
    this.entityId = entityId;
    this.entityName = entityName;
  }

  public static List<ActivityEvent> createActivityEvents(AuditEvent auditEvent) {
    return auditEvent.getEntities().stream()
        .map(auditEventEntity -> new ActivityEvent(auditEventEntity.getId(),
            auditEventEntity.getName()))
        .map(activityEvent -> buildActivityEvent(auditEvent, activityEvent))
        .collect(Collectors.toList());
  }

  private static ActivityEvent buildActivityEvent(AuditEvent auditEvent,
      ActivityEvent activityEvent) {
    activityEvent.setTimestamp(auditEvent.getTimestamp());
    activityEvent.setAction(auditEvent.getAction());
    activityEvent.setInitiatorId(auditEvent.getInitiatorId());
    activityEvent.setDescription(auditEvent.getDescription());
    activityEvent.setEntityType(auditEvent.getEntityType());

    return activityEvent;
  }
}
