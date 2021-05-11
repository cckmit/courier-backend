package com.sms.satp.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.sms.satp.common.enums.ApiJsonType;
import com.sms.satp.common.enums.ApiProtocol;
import com.sms.satp.common.enums.ApiRequestParamType;
import com.sms.satp.common.enums.ApiStatus;
import com.sms.satp.common.enums.RequestMethod;
import com.sms.satp.dto.ApiRequestDto;
import com.sms.satp.dto.ApiResponseDto;
import com.sms.satp.entity.api.ApiEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for ApiMapper")
class ApiMapperTest {

    private ApiMapper apiMapper = new ApiMapperImpl(new ParamInfoMapperImpl());

    private static final Integer SIZE = 10;
    private static final String API_NAME = "apiName";
    ApiEntity api = ApiEntity.builder().apiProtocol(ApiProtocol.HTTPS).requestMethod(RequestMethod.DELETE)
        .apiStatus(ApiStatus.DEVELOP).apiRequestJsonType(ApiJsonType.ARRAY)
        .apiRequestParamType(ApiRequestParamType.JSON).apiRequestJsonType(ApiJsonType.ARRAY)
        .apiResponseJsonType(ApiJsonType.ARRAY).createDateTime(CREATE_TIME)
        .modifyDateTime(MODIFY_TIME).apiName(API_NAME).build();
    private static final LocalDateTime CREATE_TIME = LocalDateTime.now();
    private static final LocalDateTime MODIFY_TIME = LocalDateTime.now();

    @Test
    @DisplayName("Test the method to convert the Api's entity object to a dto object")
    void entity_to_dto() {
        ApiResponseDto apiDto = apiMapper.toDto(api);
        assertThat(apiDto.getApiProtocol()).isEqualTo(ApiProtocol.HTTPS.getCode());
        assertThat(apiDto.getRequestMethod()).isEqualTo(RequestMethod.DELETE.getCode());
        assertThat(apiDto.getApiStatus()).isEqualTo(ApiStatus.DEVELOP.getCode());
        assertThat(apiDto.getCreateDateTime()).isEqualTo(CREATE_TIME);
        assertThat(apiDto.getModifyDateTime()).isEqualTo(MODIFY_TIME);
    }

    @Test
    @DisplayName("Test the method for converting an Api entity list object to a dto list object")
    void apiList_to_apiDtoList() {
        List<ApiEntity> apis = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            apis.add(api);
        }
        List<ApiResponseDto> apiDtoList = apiMapper.toDtoList(apis);
        assertThat(apiDtoList).hasSize(SIZE);
        assertThat(apiDtoList).allMatch(result -> StringUtils.equals(result.getApiName(), API_NAME));
    }

    @Test
    @DisplayName("Test the method to convert the Api's dto object to a entity object")
    void dto_to_entity() {
        ApiRequestDto apiDto = ApiRequestDto.builder()
            .apiProtocol(1).apiRequestJsonType(1).apiRequestParamType(1)
            .apiStatus(1).apiResponseJsonType(1).requestMethod(1).build();
        ApiEntity api = apiMapper.toEntity(apiDto);
        assertThat(api.getApiProtocol().getCode()).isEqualTo(1);
        assertThat(api.getApiRequestJsonType().getCode()).isEqualTo(1);
        assertThat(api.getApiRequestParamType().getCode()).isEqualTo(1);
        assertThat(api.getApiStatus().getCode()).isEqualTo(1);
        assertThat(api.getApiResponseJsonType().getCode()).isEqualTo(1);
        assertThat(api.getRequestMethod().getCode()).isEqualTo(1);
    }

    @Test
    @DisplayName("[Null Input Parameter]Test the method to convert the Api's entity object to a dto object")
    void null_entity_to_dto() {
        ApiResponseDto apiDto = apiMapper.toDto(null);
        assertThat(apiDto).isNull();
    }

    @Test
    @DisplayName("[Null Input Parameter]Test the method to convert the Api's dto object to a entity object")
    void null_dto_to_entity() {
        ApiEntity api = apiMapper.toEntity(null);
        assertThat(api).isNull();
    }

    @Test
    @DisplayName("[Null Input Parameter]Test the method for converting an Api entity list object to a dto list object")
    void null_entityList_to_dtoList() {
        List<ApiResponseDto> apiDtoList = apiMapper.toDtoList(null);
        assertThat(apiDtoList).isNull();
    }

}