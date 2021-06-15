package com.sms.satp.mapper;

import com.sms.satp.dto.request.ProjectFunctionRequest;
import com.sms.satp.dto.response.ProjectFunctionResponse;
import com.sms.satp.entity.function.ProjectFunction;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = ParamInfoMapper.class)
public interface ProjectFunctionMapper {

    ProjectFunctionResponse toDto(ProjectFunction projectFunction);

    List<ProjectFunctionResponse> toDtoList(List<ProjectFunction> projectFunctions);

    ProjectFunction toEntity(ProjectFunctionRequest projectFunctionDto);
}