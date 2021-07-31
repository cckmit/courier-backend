package com.sms.satp.repository.impl;

import static com.sms.satp.common.field.ApiTag.GROUP_NAME;
import static com.sms.satp.common.field.ApiTag.TAG_NAME;
import static com.sms.satp.common.field.CommonField.CREATE_USER_ID;
import static com.sms.satp.common.field.CommonField.ID;
import static com.sms.satp.common.field.CommonField.PROJECT_ID;
import static com.sms.satp.common.field.CommonField.REMOVE;
import static com.sms.satp.common.field.CommonField.USERNAME;
import static com.sms.satp.common.field.SceneField.CREATE_USER_NAME;
import static com.sms.satp.common.field.SceneField.GROUP_ID;
import static com.sms.satp.common.field.SceneField.NAME;
import static com.sms.satp.common.field.SceneField.PRIORITY;
import static com.sms.satp.common.field.SceneField.TAG_ID;
import static com.sms.satp.common.field.SceneField.TEST_STATUS;

import com.google.common.collect.Lists;
import com.sms.satp.common.enums.OperationModule;
import com.sms.satp.common.field.CommonField;
import com.sms.satp.dto.request.CaseTemplateSearchRequest;
import com.sms.satp.dto.response.CaseTemplateResponse;
import com.sms.satp.entity.mongo.LookupField;
import com.sms.satp.entity.mongo.LookupVo;
import com.sms.satp.entity.mongo.QueryVo;
import com.sms.satp.entity.scenetest.CaseTemplateEntity;
import com.sms.satp.repository.CommonRepository;
import com.sms.satp.repository.CustomizedCaseTemplateRepository;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class CustomizedCaseTemplateRepositoryImpl implements CustomizedCaseTemplateRepository {

    private final MongoTemplate mongoTemplate;
    private final CommonRepository commonRepository;

    public CustomizedCaseTemplateRepositoryImpl(MongoTemplate mongoTemplate,
        CommonRepository commonRepository) {
        this.mongoTemplate = mongoTemplate;
        this.commonRepository = commonRepository;
    }

    @Override
    public Page<CaseTemplateResponse> page(CaseTemplateSearchRequest searchRequest, ObjectId projectId) {
        List<LookupVo> lookupVoList = getLookupVoList();
        QueryVo queryVo = QueryVo.builder()
            .collectionName("CaseTemplate")
            .lookupVo(lookupVoList)
            .criteriaList(buildCriteria(searchRequest, projectId))
            .build();
        return commonRepository.page(queryVo, searchRequest, CaseTemplateResponse.class);
    }

    @Override
    public Boolean deleteByIds(List<String> ids) {
        return commonRepository.deleteByIds(ids, CaseTemplateEntity.class);
    }

    @Override
    public Boolean recover(List<String> ids) {
        return commonRepository.recover(ids, CaseTemplateEntity.class);
    }

    @Override
    public List<CaseTemplateEntity> getCaseTemplateIdsByGroupIds(List<String> ids) {
        Query query = new Query();
        query.fields().include(ID.getName());
        CommonField.GROUP_ID.in(ids).ifPresent(query::addCriteria);
        return mongoTemplate.find(query, CaseTemplateEntity.class);
    }

    @Override
    public Boolean deleteGroupIdByIds(List<String> ids) {
        return commonRepository.deleteFieldByIds(ids, CommonField.GROUP_ID.getName(), CaseTemplateEntity.class);
    }

    private List<LookupVo> getLookupVoList() {
        return Lists.newArrayList(
            LookupVo.builder()
                .from(OperationModule.CASE_TEMPLATE_GROUP)
                .localField(GROUP_ID)
                .foreignField(ID)
                .as("caseTemplateGroup")
                .queryFields(Lists.newArrayList(LookupField.builder().field(GROUP_NAME).alias("groupName").build()))
                .build(),
            LookupVo.builder()
                .from(OperationModule.API_TAG)
                .localField(TAG_ID)
                .foreignField(ID)
                .as("apiTag")
                .queryFields(Lists.newArrayList(LookupField.builder().field(TAG_NAME).build()))
                .build(),
            LookupVo.builder()
                .from(OperationModule.USER)
                .localField(CREATE_USER_ID)
                .foreignField(ID)
                .as("user")
                .queryFields(Lists.newArrayList(LookupField.builder().field(USERNAME).alias("createUsername").build()))
                .build()
        );
    }

    private List<Optional<Criteria>> buildCriteria(CaseTemplateSearchRequest searchRequest, ObjectId projectId) {
        return Lists.newArrayList(
            PROJECT_ID.is(projectId),
            REMOVE.is(searchRequest.isRemoved()),
            NAME.like(searchRequest.getName()),
            GROUP_ID.is(searchRequest.getGroupId()),
            TEST_STATUS.in(searchRequest.getTestStatus()),
            TAG_ID.in(searchRequest.getTagId()),
            PRIORITY.in(searchRequest.getPriority()),
            CREATE_USER_NAME.in(searchRequest.getCreateUserName())
        );
    }

}
