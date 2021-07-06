package com.sms.satp.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


import com.sms.satp.common.enums.ApiJsonType;
import com.sms.satp.common.enums.ApiProtocol;
import com.sms.satp.common.enums.ApiRequestParamType;
import com.sms.satp.common.enums.ApiType;
import com.sms.satp.common.enums.RequestMethod;
import com.sms.satp.dto.request.ApiTestCaseRequest;
import com.sms.satp.dto.response.ApiTestCaseResponse;
import com.sms.satp.entity.apitestcase.ApiTestCase;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for ApiTestCaseMapper")
class ApiTestCaseMapperTest {

    private ResponseResultVerificationMapper responseResultVerificationMapper =
        mock(ResponseResultVerificationMapper.class);
    private ResponseHeadersVerificationMapper responseHeadersVerificationMapper =
        mock(ResponseHeadersVerificationMapper.class);
    private ParamInfoMapper paramInfoMapper = new ParamInfoMapperImpl();
    private ApiTestCaseMapper apiTestCaseMapper = new ApiTestCaseMapperImpl(responseResultVerificationMapper,
        responseHeadersVerificationMapper, paramInfoMapper);
    private ApiTestCase apiTestCase = ApiTestCase.builder()
        .caseName(CASE_NAME).apiProtocol(ApiProtocol.HTTP).createDateTime(CREATE_TIME).modifyDateTime(MODIFY_TIME)
        .apiRequestJsonType(ApiJsonType.OBJECT).apiResponseJsonType(ApiJsonType.ARRAY).requestMethod(RequestMethod.GET)
        .apiRequestParamType(ApiRequestParamType.JSON).build();
    private static final Integer SIZE = 10;
    private static final String CASE_NAME = "apiTestCase";
    private static final LocalDateTime CREATE_TIME = LocalDateTime.now();
    private static final LocalDateTime MODIFY_TIME = LocalDateTime.now();

    @Test
    @DisplayName("Test the method to convert the ApiTestCase's entity object to a dto object")
    void entity_to_dto() {
        ApiTestCaseResponse apiTestCaseResponse = apiTestCaseMapper.toDto(apiTestCase);
        assertThat(apiTestCaseResponse.getCaseName()).isEqualTo(CASE_NAME);
        assertThat(apiTestCaseResponse.getApiProtocol()).isEqualTo(ApiProtocol.HTTP.getCode());
    }

    @Test
    @DisplayName("Test the method for converting an ApiTestCase entity list object to a dto list object")
    void apiTestCaseList_to_apiTestCaseDtoList() {
        List<ApiTestCase> apiTestCases = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            apiTestCases.add(apiTestCase);
        }
        List<ApiTestCaseResponse> apiTestCaseResponseList = apiTestCaseMapper.toDtoList(apiTestCases);
        assertThat(apiTestCaseResponseList).hasSize(SIZE);
        assertThat(apiTestCaseResponseList).allMatch(result -> StringUtils.equals(result.getCaseName(), CASE_NAME));
    }

    @Test
    @DisplayName("Test the method to convert the ApiTestCase's dto object to a entity object")
    void dto_to_entity() {
        ApiTestCaseRequest apiTestCaseRequest = ApiTestCaseRequest.builder()
            .caseName(CASE_NAME)
            .build();
        ApiTestCase apiTestCase = apiTestCaseMapper.toEntity(apiTestCaseRequest);
        assertThat(apiTestCase.getCaseName()).isEqualTo(CASE_NAME);
    }

    @Test
    @DisplayName("[Null Input Parameter]Test the method to convert the ApiTestCase's entity object to a dto object")
    void null_entity_to_dto() {
        ApiTestCaseResponse apiTestCaseResponse = apiTestCaseMapper.toDto(null);
        assertThat(apiTestCaseResponse).isNull();
    }

    @Test
    @DisplayName("[Null Input Parameter]Test the method to convert the ApiTestCase's dto object to a entity object")
    void null_dto_to_entity() {
        ApiTestCase apiTestCase = apiTestCaseMapper.toEntity(null);
        assertThat(apiTestCase).isNull();
    }

    @Test
    @DisplayName("[Null Input Parameter]Test the method for converting an ApiTestCase entity list object to a dto list object")
    void null_entityList_to_dtoList() {
        List<ApiTestCaseResponse> apiTestCaseDtoList = apiTestCaseMapper.toDtoList(null);
        assertThat(apiTestCaseDtoList).isNull();
    }

}