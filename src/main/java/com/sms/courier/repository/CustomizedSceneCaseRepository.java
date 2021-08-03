package com.sms.courier.repository;

import com.sms.courier.dto.request.SearchSceneCaseRequest;
import com.sms.courier.dto.response.SceneCaseResponse;
import com.sms.courier.entity.scenetest.SceneCaseEntity;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;

public interface CustomizedSceneCaseRepository {

    Page<SceneCaseResponse> search(SearchSceneCaseRequest searchDto, ObjectId projectId);

    Boolean deleteByIds(List<String> ids);

    Boolean recover(List<String> ids);

    List<SceneCaseEntity> getSceneCaseIdsByGroupIds(List<String> ids);

    Boolean deleteGroupIdByIds(List<String> ids);
}