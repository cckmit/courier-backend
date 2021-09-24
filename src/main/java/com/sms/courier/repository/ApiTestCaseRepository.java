package com.sms.courier.repository;

import com.sms.courier.entity.apitestcase.ApiTestCaseEntity;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApiTestCaseRepository extends MongoRepository<ApiTestCaseEntity, String> {

    void deleteAllByIdIn(List<String> ids);

    void deleteAllByRemovedIsTrue();

    List<ApiTestCaseEntity> findByRemovedIsFalse();

    List<ApiTestCaseEntity> findByIdIn(List<String> ids);

    List<ApiTestCaseEntity> findByTagIdIn(List<String> tagIds);

}