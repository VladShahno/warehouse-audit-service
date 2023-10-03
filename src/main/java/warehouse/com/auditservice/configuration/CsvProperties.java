package warehouse.com.auditservice.configuration;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "csv")
public class CsvProperties {

  private List<String> responseHeaders;
  private List<String> responseFields;
  private int exportCsvLimit;
}
