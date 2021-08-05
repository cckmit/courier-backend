package com.sms.courier.mapper;

import com.google.common.collect.Lists;
import com.sms.courier.common.enums.*;
import com.sms.courier.dto.request.*;
import com.sms.courier.dto.response.SceneCaseApiConnResponse;
import com.sms.courier.dto.response.SceneCaseApiResponse;
import com.sms.courier.entity.api.ApiEntity;
import com.sms.courier.entity.apitestcase.ApiTestCaseEntity;
import com.sms.courier.entity.scenetest.SceneCaseApiEntity;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.assertj.core.api.InstanceOfAssertFactories.list;
import static org.mockito.Mockito.mock;

@DisplayName("Tests for SceneCaseApiMapper")
class SceneCaseApiMapperTest {

    private ApiTestCaseMapper apiTestCaseMapper = mock(ApiTestCaseMapper.class);
    private SceneCaseApiMapper sceneCaseApiMapper = new SceneCaseApiMapperImpl(apiTestCaseMapper);
    private static final String MOCK_ID = "1";

    @Test
    @DisplayName("Test the toSceneCaseApiByUpdateRequest method in the SceneCaseApiMapper")
    void toSceneCaseApiByUpdateRequest_test() {
        UpdateSceneCaseApiRequest updateSceneCaseApiRequest = UpdateSceneCaseApiRequest.builder().id(MOCK_ID).build();
        SceneCaseApiEntity sceneCaseApi = sceneCaseApiMapper.toSceneCaseApiByUpdateRequest(updateSceneCaseApiRequest);
        assertThat(sceneCaseApi.getId()).isEqualTo(MOCK_ID);
    }

    @Test
    @DisplayName("Test the toSceneCaseApiDto method in the SceneCaseApiMapper")
    void toSceneCaseApiDto_test() {
        SceneCaseApiEntity sceneCaseApi = SceneCaseApiEntity.builder().id(MOCK_ID).sceneCaseId(MOCK_ID)
                .apiType(ApiType.API)
                .apiTestCase(ApiTestCaseEntity.builder()
                        .apiEntity(ApiEntity.builder()
                                .apiProtocol(ApiProtocol.HTTPS)
                                .requestMethod(RequestMethod.GET)
                                .apiRequestParamType(ApiRequestParamType.FORM_DATA)
                                .apiResponseJsonType(ApiJsonType.OBJECT)
                                .apiRequestJsonType(ApiJsonType.OBJECT)
                                .build())
                        .build())
                .build();
        SceneCaseApiResponse dto = sceneCaseApiMapper.toSceneCaseApiDto(sceneCaseApi);
        assertThat(dto.getId()).isEqualTo(MOCK_ID);
    }

    @Test
    @DisplayName("Test the toSceneCaseApiList method in the SceneCaseApiMapper")
    void toSceneCaseApiList_test() {
        List<UpdateSceneCaseApiRequest> sceneCaseApiList =
                Lists.newArrayList(UpdateSceneCaseApiRequest.builder().id(MOCK_ID).build());
        List<SceneCaseApiEntity> apiList = sceneCaseApiMapper.toSceneCaseApiList(sceneCaseApiList);
        assertThat(apiList.size()).isEqualTo(sceneCaseApiList.size());
    }

    @Test
    @DisplayName("Test the toSceneCaseApiListByAddRequest method in the SceneCaseApiMapper")
    void toSceneCaseApiListByAddRequest_test() {
        List<AddSceneCaseApiRequest> addSceneCaseApiRequestList =
                Lists.newArrayList(AddSceneCaseApiRequest.builder().build());
        List<SceneCaseApiEntity> sceneCaseApiList =
                sceneCaseApiMapper.toSceneCaseApiListByAddRequest(addSceneCaseApiRequestList);
        assertThat(sceneCaseApiList).isNotEmpty();
    }

    @Test
    @DisplayName("Test the toSceneCaseApiByUpdateRequest_isNull method in the SceneCaseApiMapper")
    void toSceneCaseApiByUpdateRequest_isNull_test() {
        SceneCaseApiEntity dto = sceneCaseApiMapper.toSceneCaseApiByUpdateRequest(null);
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("Test the toSceneCaseApiDto_isNull method in the SceneCaseApiMapper")
    void toSceneCaseApiDto_isNull_test() {
        SceneCaseApiResponse dto = sceneCaseApiMapper.toSceneCaseApiDto(null);
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("Test the toSceneCaseApiList_isNull method in the SceneCaseApiMapper")
    void toSceneCaseApiList_isNull_test() {
        assertThat(sceneCaseApiMapper.toSceneCaseApiList(null)).isNull();
    }

    @Test
    @DisplayName("Test the toSceneCaseApiListByAddRequest_isNull_test method in the SceneCaseApiMapper")
    void toSceneCaseApiListByAddRequest_isNull_test() {
        assertThat(sceneCaseApiMapper.toSceneCaseApiListByAddRequest(null)).isNull();
    }

    @Test
    @DisplayName("Test the toSceneCaseApi_isNull_test method in the SceneCaseApiMapper")
    void toSceneCaseApi_isNull_test() {
        assertThat(sceneCaseApiMapper.toSceneCaseApi(null)).isNull();
    }

    @Test
    @DisplayName("Test the toSceneCaseApiConnResponse test method in the SceneCaseApiMapper")
    void toSceneCaseApiConnResponse_test() {
        SceneCaseApiEntity sceneCaseApi = SceneCaseApiEntity.builder().build();
        SceneCaseApiConnResponse dto = sceneCaseApiMapper.toSceneCaseApiConnResponse(sceneCaseApi);
        assertThat(dto).isNotNull();
    }

    @Test
    @DisplayName("Test the toSceneCaseApiConnResponse test method in the SceneCaseApiMapper")
    void toSceneCaseApiConnResponse_IsNull_test() {
        SceneCaseApiConnResponse dto = sceneCaseApiMapper.toSceneCaseApiConnResponse(null);
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("Test the apiTestCaseRequestToApiTestCaseEntity test method in the SceneCaseApiMapper")
    void apiTestCaseRequestToApiTestCaseEntity_test() {
        AddSceneCaseApiRequest addSceneCaseApiRequest = AddSceneCaseApiRequest.builder()
                .apiTestCase(ApiTestCaseRequest.builder()
                        .apiEntity(ApiEntityRequest.builder()
                                .requestHeaders(Lists.newArrayList(ParamInfoRequest.builder()
                                        .paramType(ParamType.INT)
                                        .build()))
                                .build())
                        .tagId(Lists.newArrayList("tagId"))
                        .build())
                .build();
        assertThat(sceneCaseApiMapper.toSceneCaseApi(addSceneCaseApiRequest)).isNotNull();
    }

    @Test
    @DisplayName("Test the apiTestCaseRequestToApiTestCaseEntity test method in the SceneCaseApiMapper")
    void paramInfoRequestToParamInfo_IsNull_test() {
        ParamInfoRequest paramInfoRequest = null;
        AddSceneCaseApiRequest addSceneCaseApiRequest = AddSceneCaseApiRequest.builder()
                .apiTestCase(ApiTestCaseRequest.builder()
                        .apiEntity(ApiEntityRequest.builder()
                                .requestHeaders(Lists.newArrayList(paramInfoRequest))
                                .tagId(Lists.newArrayList())
                                .build())
                        .build())
                .build();
        assertThat(sceneCaseApiMapper.toSceneCaseApi(addSceneCaseApiRequest)).isNotNull();
    }

    @Test
    @DisplayName("Test the apiTestCaseRequestToApiTestCaseEntity test method in the SceneCaseApiMapper")
    void apiTestCaseRequestToApiTestCaseEntity_Null_test() {
        AddSceneCaseApiRequest addSceneCaseApiRequest = AddSceneCaseApiRequest.builder()
                .apiTestCase(ApiTestCaseRequest.builder()
                        .apiEntity(null)
                        .build())
                .build();
        assertThat(sceneCaseApiMapper.toSceneCaseApi(addSceneCaseApiRequest)).isNotNull();
    }

}
