package com.sms.satp.entity.dto;

import com.sms.satp.dto.SceneCaseApiDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSceneCaseApiDto {

    private List<SceneCaseApiDto> sceneCaseApiDtoList;
}
