package warehouse.com.auditservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import warehouse.com.auditservice.model.ActivityEvent;

@Repository
public interface ActivityEventRepository extends MongoRepository<ActivityEvent, String>,
    ActivityEventCustomRepository {

}
