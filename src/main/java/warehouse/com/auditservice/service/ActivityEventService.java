package warehouse.com.auditservice.service;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import warehouse.com.auditservice.configuration.CsvProperties;
import warehouse.com.auditservice.model.ActivityEvent;
import warehouse.com.auditservice.model.dto.ActivityEventDto;
import warehouse.com.auditservice.model.dto.ActivityEventRequest;
import warehouse.com.auditservice.model.dto.BodyActivityEventRequest;
import warehouse.com.auditservice.repository.ActivityEventRepository;
import warehouse.com.csv.service.CsvService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityEventService {

  private final CsvService csvService;
  private final CsvProperties csvProperties;
  private final ActivityEventRepository activityEventRepository;

  public Page<ActivityEventDto> getActivityEvents(ActivityEventRequest request, Pageable pageable) {
    return activityEventRepository.findAll(request, request.getEntityType(), pageable)
        .map(ActivityEventDto::new);
  }

  public void saveEventEntity(List<ActivityEvent> activityEvents) {
    activityEventRepository.saveAll(activityEvents);
    log.debug("Saved activity events with {} and {}",
        keyValue("eventsCount", activityEvents.size()),
        keyValue("entityIds",
            activityEvents.stream().map(ActivityEvent::getEntityId).collect(Collectors.toList())));
  }

  public void exportCsv(Writer writer, ActivityEventRequest request,
      BodyActivityEventRequest bodyActivityEventRequest, Sort sort) {

    var activityEvents = activityEventRepository.findAllForCsv(request, bodyActivityEventRequest,
        request.getEntityType(), csvProperties.getExportCsvLimit(), sort);

    csvService.exportDataToWriter(writer, activityEvents,
        csvProperties.getResponseHeaders(), csvProperties.getResponseFields());
  }
}
