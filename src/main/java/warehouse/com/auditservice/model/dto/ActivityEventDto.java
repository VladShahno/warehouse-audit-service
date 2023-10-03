package warehouse.com.auditservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import warehouse.com.auditservice.model.ActivityEvent;

@Data
@Schema(description = "Represents the necessary information about the events that was happened.")
public class ActivityEventDto {

  @Schema(description = "A Unique id of the object")
  private String activityEventId;
  @Schema(description = "The initiator id.")
  private String initiatorId;
  @Schema(description = "The action that created an event.")
  private String action;
  @Schema(description = "The description of event.")
  private String description;
  @Schema(description = "Date and time when event was happened.")
  private Date timestamp;
  @Schema(description = "The entity id")
  private String entityId;
  @Schema(description = "The type of entity for example - product, warehouse, order ... etc.")
  private String entityType;
  @Schema(description = "The name of entity.")
  private String entityName;

  public ActivityEventDto(ActivityEvent activityEvent) {
    BeanUtils.copyProperties(activityEvent, this);
  }

}
