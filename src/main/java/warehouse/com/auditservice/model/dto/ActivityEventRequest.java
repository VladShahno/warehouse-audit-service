package warehouse.com.auditservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import java.util.Set;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Schema(description = "Represents an object that contains query parameters.")
public class ActivityEventRequest {

  @Schema(description = "Receives any action's names and its' parts, it is a case insensitive, also supports special characters '*' and '?'", example = "Update")
  private String actionSearch;
  @Schema(description = "Must match entity type.", example = "USER")
  private Set<String> entityType;
  @Schema(description = "Must match entity id.", example = "7bdecaaf-5505-4934-a09b-28cd6c979f1c")
  private String entityId;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @Schema(description = "End date - must be in yyyy-MM-dd'T'HH:mm:ss.SSS'Z' format", example = "2020-05-25T14:05:15.953Z")
  private Date to;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @Schema(description = "Start date - must be in yyyy-MM-dd'T'HH:mm:ss.SSS'Z' format", example = "2018-04-25T14:05:15.953Z")
  private Date from;
}
