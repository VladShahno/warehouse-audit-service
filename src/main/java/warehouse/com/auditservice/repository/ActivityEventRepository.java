package warehouse.com.auditservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import warehouse.com.auditservice.model.ActivityEvent;

public interface ActivityEventRepository extends MongoRepository<ActivityEvent, String>,
    ActivityEventCustomRepository {

}
