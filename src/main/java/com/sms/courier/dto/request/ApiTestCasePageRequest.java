package com.sms.courier.dto.request;

import com.sms.courier.common.enums.ApiBindingStatus;
import com.sms.courier.dto.PageDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ApiTestCasePageRequest extends PageDto {

    private String projectId;
    private String caseName;
    private List<String> tagId;
    private ApiBindingStatus status;
    private String apiId;
}
