package com.sms.satp.repository.impl;

import static com.sms.satp.common.field.ApiFiled.API_PROTOCOL;
import static com.sms.satp.common.field.ApiFiled.API_REQUEST_PARAM_TYPE;
import static com.sms.satp.common.field.ApiFiled.API_STATUS;
import static com.sms.satp.common.field.ApiFiled.GROUP_ID;
import static com.sms.satp.common.field.ApiFiled.REQUEST_METHOD;
import static com.sms.satp.common.field.ApiFiled.TAG_ID;
import static com.sms.satp.common.field.CommonFiled.ID;
import static com.sms.satp.common.field.CommonFiled.PROJECT_ID;
import static com.sms.satp.common.field.CommonFiled.REMOVE;

import com.sms.satp.dto.request.ApiPageRequest;
import com.sms.satp.dto.response.ApiResponse;
import com.sms.satp.entity.api.ApiEntity;
import com.sms.satp.repository.CommonDeleteRepository;
import com.sms.satp.repository.CustomizedApiRepository;
import com.sms.satp.utils.PageDtoConverter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class CustomizedApiRepositoryImpl implements CustomizedApiRepository {

    private final MongoTemplate mongoTemplate;
    private final CommonDeleteRepository commonDeleteRepository;

    public CustomizedApiRepositoryImpl(MongoTemplate mongoTemplate, CommonDeleteRepository commonDeleteRepository) {
        this.mongoTemplate = mongoTemplate;
        this.commonDeleteRepository = commonDeleteRepository;
    }


    @Override
    public Page<ApiResponse> page(ApiPageRequest apiPageRequest) {
        PageDtoConverter.frontMapping(apiPageRequest);
        ArrayList<AggregationOperation> aggregationOperations = new ArrayList<>();
        Query query = new Query();

        LookupOperation apiTagLookupOperation =
            LookupOperation.newLookup().from("ApiTag").localField(TAG_ID.getFiled())
                .foreignField(ID.getFiled())
                .as("apiTag");
        LookupOperation apiGroupLookupOperation =
            LookupOperation.newLookup().from("ApiGroup").localField(GROUP_ID.getFiled())
                .foreignField(ID.getFiled())
                .as("apiGroup");
        aggregationOperations.add(apiTagLookupOperation);
        aggregationOperations.add(apiGroupLookupOperation);

        buildCriteria(apiPageRequest, query, aggregationOperations);

        Sort sort = PageDtoConverter.createSort(apiPageRequest);
        aggregationOperations.add(Aggregation.sort(sort));

        int skipRecord = apiPageRequest.getPageNumber() * apiPageRequest.getPageSize();
        aggregationOperations.add(Aggregation.skip(Long.valueOf(skipRecord)));
        aggregationOperations.add(Aggregation.limit(apiPageRequest.getPageSize()));

        ProjectionOperation projectionOperation = Aggregation.project(ApiResponse.class);
        projectionOperation = projectionOperation.and("apiTag.tagName").as("tagName");
        projectionOperation = projectionOperation.and("apiGroup.name").as("groupName");
        aggregationOperations.add(projectionOperation);

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        long count = mongoTemplate.count(query, ApiEntity.class);
        if (count == 0L || skipRecord >= count) {
            return Page.empty();
        }
        List<ApiResponse> records = mongoTemplate.aggregate(aggregation, ApiEntity.class, ApiResponse.class)
            .getMappedResults();
        return new PageImpl<ApiResponse>(records,
            PageRequest.of(apiPageRequest.getPageNumber(), apiPageRequest.getPageSize(), sort), count);
    }

    @Override
    public Boolean deleteById(String id) {
        return commonDeleteRepository.deleteById(id, ApiEntity.class);
    }

    @Override
    public Boolean deleteByIds(List<String> ids) {
        return commonDeleteRepository.deleteByIds(ids, ApiEntity.class);
    }

    private void buildCriteria(ApiPageRequest apiPageRequest, Query query,
        List<AggregationOperation> aggregationOperations) {
        REMOVE.is(Boolean.FALSE)
            .ifPresent(criteria -> addCriteria(criteria, query, aggregationOperations));
        PROJECT_ID.is(apiPageRequest.getProjectId())
            .ifPresent(criteria -> addCriteria(criteria, query, aggregationOperations));
        API_PROTOCOL.in(apiPageRequest.getApiProtocol())
            .ifPresent(criteria -> addCriteria(criteria, query, aggregationOperations));
        API_REQUEST_PARAM_TYPE.in(apiPageRequest.getApiRequestParamType())
            .ifPresent(criteria -> addCriteria(criteria, query, aggregationOperations));
        API_STATUS.in(apiPageRequest.getApiStatus())
            .ifPresent(criteria -> addCriteria(criteria, query, aggregationOperations));
        GROUP_ID.in(apiPageRequest.getGroupId())
            .ifPresent(criteria -> addCriteria(criteria, query, aggregationOperations));
        REQUEST_METHOD.in(apiPageRequest.getRequestMethod())
            .ifPresent(criteria -> addCriteria(criteria, query, aggregationOperations));
        TAG_ID.in(apiPageRequest.getTagId()).ifPresent(criteria -> addCriteria(criteria, query, aggregationOperations));
    }

    private void addCriteria(Criteria criteria, Query query, List<AggregationOperation> aggregationOperations) {
        query.addCriteria(criteria);
        aggregationOperations.add(Aggregation.match(criteria));
    }

}