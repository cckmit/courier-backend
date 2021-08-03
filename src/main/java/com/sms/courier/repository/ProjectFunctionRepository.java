package com.sms.courier.repository;

import com.sms.courier.dto.response.ProjectFunctionResponse;
import com.sms.courier.entity.function.ProjectFunctionEntity;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectFunctionRepository extends MongoRepository<ProjectFunctionEntity, String> {

    Stream<ProjectFunctionResponse> findAllByRemovedIsFalse();

    List<ProjectFunctionResponse> findAllByIdIn(List<String> ids);

    boolean existsByFunctionKeyAndProjectIdAndRemovedIsFalse(String functionName, String projectId);

}