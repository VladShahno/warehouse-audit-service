package warehouse.com.auditservice.configuration;

import java.util.Map;
import java.util.Set;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("entity-type")
@Data
public class EntityTypeConfig {

  private Map<String, String> roleMapping;
  private Set<String> orgIndependentTypes;
}
