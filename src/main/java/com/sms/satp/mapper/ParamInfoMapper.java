package com.sms.satp.mapper;

import com.sms.satp.dto.request.ParamInfoRequest;
import com.sms.satp.dto.response.ParamInfoResponse;
import com.sms.satp.entity.api.common.ParamInfo;
import com.sms.satp.utils.EnumCommonUtils;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = EnumCommonUtils.class)
public interface ParamInfoMapper {

    ParamInfo toEntity(ParamInfoRequest paramInfoRequest);

    @Mapping(target = "paramType", expression = "java(com.sms.satp.common.enums.ParamType"
        + ".getType(paramInfoResponse.getParamType()))")
    ParamInfo toEntityByResponse(ParamInfoResponse paramInfoResponse);

    List<ParamInfo> toEntityList(List<ParamInfoRequest> paramInfoDtoList);

    ParamInfoResponse toDto(ParamInfo paramInfo);

    List<ParamInfoResponse> toDtoList(List<ParamInfo> paramInfoList);


}