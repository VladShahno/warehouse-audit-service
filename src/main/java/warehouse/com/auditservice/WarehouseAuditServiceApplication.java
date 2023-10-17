package warehouse.com.auditservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import warehouse.com.auditservice.configuration.CsvProperties;
import warehouse.com.auditservice.configuration.EntityTypeConfig;

@SpringBootApplication
@ComponentScan(basePackages = "warehouse.com")
@EnableConfigurationProperties({EntityTypeConfig.class, CsvProperties.class})
public class WarehouseAuditServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(WarehouseAuditServiceApplication.class, args);
  }

}
