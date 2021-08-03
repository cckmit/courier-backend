package com.sms.courier.repository.impl;

import static com.sms.courier.common.field.CommonField.CREATE_DATE_TIME;
import static com.sms.courier.common.field.CommonField.PROJECT_ID;
import static com.sms.courier.common.field.LogField.OPERATION_DESC;
import static com.sms.courier.common.field.LogField.OPERATION_MODULE;
import static com.sms.courier.common.field.LogField.OPERATION_TYPE;
import static com.sms.courier.common.field.LogField.OPERATOR;
import static com.sms.courier.common.field.LogField.OPERATOR_ID;

import com.sms.courier.dto.request.LogPageRequest;
import com.sms.courier.entity.log.LogEntity;
import com.sms.courier.repository.CustomizedLogRepository;
import com.sms.courier.utils.PageDtoConverter;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class CustomizedLogRepositoryImpl implements CustomizedLogRepository {

    private final MongoTemplate mongoTemplate;

    public CustomizedLogRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<LogEntity> page(LogPageRequest logPageRequest) {
        PageDtoConverter.frontMapping(logPageRequest);
        Query query = new Query();
        CREATE_DATE_TIME.lteAndGte(logPageRequest.getQueryBeginTime(), logPageRequest.getQueryEndTime())
            .ifPresent(query::addCriteria);
        PROJECT_ID.projectIdIs(logPageRequest.getProjectId()).ifPresent(query::addCriteria);
        OPERATION_DESC.is(logPageRequest.getOperationDesc()).ifPresent(query::addCriteria);
        OPERATION_TYPE.is(logPageRequest.getOperationType()).ifPresent(query::addCriteria);
        OPERATION_MODULE.is(logPageRequest.getOperationModule()).ifPresent(query::addCriteria);
        OPERATOR.is(logPageRequest.getOperator()).ifPresent(query::addCriteria);
        OPERATOR_ID.is(logPageRequest.getOperatorId()).ifPresent(query::addCriteria);
        long count = mongoTemplate.count(query, LogEntity.class);
        Pageable pageable = PageDtoConverter.createPageable(logPageRequest);
        List<LogEntity> logList = mongoTemplate.find(query.with(pageable), LogEntity.class);
        return new PageImpl<>(logList, pageable, count);
    }
}