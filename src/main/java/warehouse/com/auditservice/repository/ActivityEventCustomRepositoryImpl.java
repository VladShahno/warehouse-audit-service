package warehouse.com.auditservice.repository;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.MongoRegexCreator;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import warehouse.com.auditservice.configuration.EntityTypeConfig;
import warehouse.com.auditservice.model.ActivityEvent;
import warehouse.com.auditservice.model.dto.ActivityEventRequest;
import warehouse.com.auditservice.model.dto.BodyActivityEventRequest;

@RequiredArgsConstructor
@Repository
public class ActivityEventCustomRepositoryImpl implements ActivityEventCustomRepository {
  public static final String FIELD_ID = "_id";
  private static final String ENTITY_TYPE_FIELD_NAME = "entityType";
  private static final String ACTION_FIELD_NAME = "action";
  private static final String ENTITY_ID_FIELD_NAME = "entityId";
  private static final String TIMESTAMP_FIELD_NAME = "timestamp";
  private static final String ACTION_FIELD_PATTERN = Pattern.compile("^[a-zA-Z*?\\-\\s]*$").pattern();
  private static final String QUESTION_MARK_REGEX = Pattern.compile("\\?").pattern();
  private static final String ASTERISK_REGEX = Pattern.compile("\\*").pattern();

  private final MongoTemplate mongoTemplate;

  private final EntityTypeConfig entityTypeConfig;

  @Override
  public Page<ActivityEvent> findAll(
      ActivityEventRequest request,
      Set<String> entityTypes,
      Pageable pageable) {
    var criteria = createCriteria(entityTypes);
    addCriteriaFromActivityEventRequest(criteria, request);

    var query = Query.query(criteria).with(pageable);
    var activityEvents = mongoTemplate.find(query, ActivityEvent.class);

    return PageableExecutionUtils.getPage(activityEvents, pageable,
        () -> mongoTemplate.count(Query.query(criteria), ActivityEvent.class));
  }

  @Override
  public List<ActivityEvent> findAllForCsv(
      ActivityEventRequest request,
      BodyActivityEventRequest bodyRequest,
      Set<String> entityTypes,
      int csvLimit,
      Sort sort) {
    var criteria = createCriteria(entityTypes);
    Optional.ofNullable(request).ifPresent(rq -> addCriteriaFromActivityEventRequest(criteria, rq));
    Optional.ofNullable(bodyRequest).ifPresent(brq -> addCriteriaFromBodyActivityEventRequest(criteria, brq));
    var query = Query.query(criteria).limit(csvLimit);
    Optional.ofNullable(sort).ifPresent(query::with);
    return mongoTemplate.find(query, ActivityEvent.class);
  }

  private Criteria createCriteria(Set<String> entityTypes) {
    var criteria = new Criteria();

    var orgIndependentTypes = entityTypeConfig.getOrgIndependentTypes().stream()
        .filter(entityTypes::contains)
        .collect(Collectors.toSet());

    if (orgIndependentTypes.isEmpty()) {
      return criteria.and(ENTITY_TYPE_FIELD_NAME).in(entityTypes);
    }

    var orgDependentTypes = new HashSet<>(entityTypes);
    orgDependentTypes.removeAll(orgIndependentTypes);

    if (orgDependentTypes.isEmpty()) {
      return criteria.and(ENTITY_TYPE_FIELD_NAME).in(orgIndependentTypes);
    }

    return criteria.orOperator(
        Criteria.where(ENTITY_TYPE_FIELD_NAME).in(orgDependentTypes),
        Criteria.where(ENTITY_TYPE_FIELD_NAME).in(orgIndependentTypes));
  }

  private void addCriteriaFromActivityEventRequest(Criteria criteria, ActivityEventRequest request) {
    Optional.ofNullable(request.getEntityId()).ifPresent(entityId -> criteria.and(ENTITY_ID_FIELD_NAME).is(entityId));
    Optional.ofNullable(request.getActionSearch()).ifPresent(action -> addActionFieldCriteria(criteria, action));
    addTimestampCriteria(request, criteria);
  }

  private void addActionFieldCriteria(Criteria criteria, String action) {
    if (action.matches(ACTION_FIELD_PATTERN)) {
      Optional.ofNullable(getPreparedRegExp(action))
          .ifPresent(regex -> criteria.and(ACTION_FIELD_NAME).regex(regex, "i"));
    }
  }

  private void addCriteriaFromBodyActivityEventRequest(Criteria criteria, BodyActivityEventRequest request) {
    Optional.ofNullable(request.getIds())
        .filter(list -> !list.isEmpty())
        .ifPresent(ids -> criteria.and(ENTITY_ID_FIELD_NAME).in(ids));
    Optional.ofNullable(request.getActivityEventIds())
        .filter(list -> !list.isEmpty())
        .ifPresent(ids -> criteria.and(FIELD_ID).in(ids));
  }

  private String getPreparedRegExp(String key) {
    String formattedQuery = key
        .replaceAll(ASTERISK_REGEX, ".*")
        .replaceAll(QUESTION_MARK_REGEX, ".");
    if (!formattedQuery.startsWith(".*")) {
      formattedQuery = "^" + formattedQuery;
    }
    return MongoRegexCreator.INSTANCE.toRegularExpression(formattedQuery, MongoRegexCreator.MatchMode.REGEX);
  }

  private void addTimestampCriteria(ActivityEventRequest request, Criteria criteria) {
    Date from = request.getFrom();
    Date to = request.getTo();
    if (from != null || to != null) {
      Criteria timestamp = criteria.and(TIMESTAMP_FIELD_NAME);
      Optional.ofNullable(from).ifPresent(timestamp::gte);
      Optional.ofNullable(to).ifPresent(timestamp::lte);
    }
  }
}
