package warehouse.com.auditservice.repository;

import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import warehouse.com.auditservice.model.ActivityEvent;
import warehouse.com.auditservice.model.dto.ActivityEventRequest;
import warehouse.com.auditservice.model.dto.BodyActivityEventRequest;

public interface ActivityEventCustomRepository {

  Page<ActivityEvent> findAll(ActivityEventRequest request, Set<String> entityTypes,
      Pageable pageable);

  List<ActivityEvent> findAllForCsv(ActivityEventRequest request,
      BodyActivityEventRequest bodyRequest, Set<String> entityTypes, int csvLimit, Sort sort);
}
