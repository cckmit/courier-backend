package com.sms.satp.entity.job.common;

import com.sms.satp.entity.api.common.AdvancedSetting;
import com.sms.satp.entity.api.common.HttpStatusVerification;
import com.sms.satp.entity.api.common.ParamInfo;
import com.sms.satp.entity.api.common.ResponseHeadersVerification;
import com.sms.satp.entity.api.common.ResponseResultVerification;
import com.sms.satp.entity.api.common.ResponseTimeVerification;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobApiTestCase {

    @Field(targetType = FieldType.OBJECT_ID)
    private String id;

    @Field(targetType = FieldType.OBJECT_ID)
    private String apiId;

    private String caseName;

    private List<String> tagId;

    private String apiName;

    private String description;

    private String apiPath;

    private Integer apiProtocol;

    private Integer requestMethod;

    private Integer apiRequestParamType;

    private List<ParamInfo> requestHeaders;
    private List<ParamInfo> pathParams;
    private List<ParamInfo> restfulParams;
    private List<ParamInfo> requestParams;
    private List<ParamInfo> responseParams;

    private Integer responseParamsExtractionType;

    private String preInject;

    private String postInject;

    private Integer apiResponseJsonType;

    private Integer apiRequestJsonType;

    private HttpStatusVerification httpStatusVerification;

    private ResponseHeadersVerification responseHeadersVerification;

    private ResponseResultVerification responseResultVerification;

    private ResponseTimeVerification responseTimeVerification;

    @Field("isExecute")
    private boolean execute;

    private String modifyUserId;

    private String modifyDateTime;

    private CaseReport caseReport;

    private long delayTime;

    private AdvancedSetting advancedSetting;

}
