package com.sms.satp.service;

import com.sms.satp.dto.request.AddSceneCaseApiByIdsRequest;
import com.sms.satp.dto.request.AddSceneCaseRequest;
import com.sms.satp.dto.request.SearchSceneCaseRequest;
import com.sms.satp.dto.request.UpdateSceneCaseRequest;
import com.sms.satp.dto.request.UpdateSceneTemplateRequest;
import com.sms.satp.dto.response.SceneCaseResponse;
import com.sms.satp.dto.response.SceneTemplateResponse;
import com.sms.satp.entity.scenetest.SceneCase;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;

public interface SceneCaseService {

    Boolean add(AddSceneCaseRequest addSceneCaseRequest);

    Boolean deleteByIds(List<String> ids);

    Boolean edit(UpdateSceneCaseRequest updateSceneCaseRequest);

    Boolean batchEdit(List<SceneCase> sceneCaseList);

    Page<SceneCaseResponse> page(SearchSceneCaseRequest searchDto, ObjectId projectId);

    SceneTemplateResponse getConn(String id);

    Boolean editConn(UpdateSceneTemplateRequest updateSceneTemplateRequest);

    List<SceneCase> get(String groupId, String projectId);

    Boolean addApi(AddSceneCaseApiByIdsRequest request);
}
