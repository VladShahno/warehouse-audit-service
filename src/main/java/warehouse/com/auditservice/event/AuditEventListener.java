package warehouse.com.auditservice.event;


import static net.logstash.logback.argument.StructuredArguments.keyValue;
import static warehouse.com.auditservice.common.Constants.AUDIT_EVENT;

import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import warehouse.com.auditservice.model.ActivityEvent;
import warehouse.com.auditservice.service.ActivityEventService;
import warehouse.com.eventstarter.annotation.EventListener;
import warehouse.com.eventstarter.model.AuditEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditEventListener {

  private final ActivityEventService activityEventService;

  @EventListener(value = "${kafka.topics.audit.name}", groupId = "audit-services-group")
  public void handleActivityEvent(AuditEvent auditEvents) {
    log.debug("Consumed {}", keyValue(AUDIT_EVENT, auditEvents));
    var activityEvents = new ArrayList<>(ActivityEvent.createActivityEvents(auditEvents));
    activityEventService.saveEventEntity(activityEvents);
  }
}
