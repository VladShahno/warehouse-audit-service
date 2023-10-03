package warehouse.com.auditservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "Represents an object that contains body request parameters.")
public class BodyActivityEventRequest {

  @Schema(name = "ids", description = "Must contain an array of ids that match entity ids.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private List<String> ids;
  @Schema(description = "Must contain an array of ids that match activity event id.", example = """
      {
       "activityEventIds":["507f1f77bcf86cd799439011","00000020f51bb4362eee2a4d"]
      }
      """, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private List<String> activityEventIds;
  @Schema(name = "filter", description = "Additional criteria to filter events by", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private ActivityEventRequest filter;
}
