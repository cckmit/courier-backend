package com.sms.satp.entity.dto;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DataParamDto {

    @NotEmpty(message = "The paramKey connot by empty")
    private String paramKey;
    private Object paramValue;
}
