package com.sms.satp.dto.request;

import com.sms.satp.common.enums.ParamType;
import com.sms.satp.common.validate.InsertGroup;
import com.sms.satp.common.validate.UpdateGroup;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParamInfoRequest {

    /**
     * 字段名.
     */
    @NotBlank(groups = {InsertGroup.class, UpdateGroup.class}, message = "The key must not be empty.")
    private String key;
    /**
     * 字段值 eg. 数组[1,2,3,4,5] 字符串 abc Json字段值在childParam里面.
     */
    private String value;
    /**
     * 字段描述.
     */
    private String description;
    /**
     * 字段类型.
     *
     * @link ParamType
     */

    private ParamType paramType;
    /**
     * 是否递归引用自己.
     */
    private Boolean reference;
    /**
     * 是否必填.
     */
    private Boolean required;
    /**
     * 是否传输.
     */
    private Boolean checkbox;
    /**
     * 对象子属性. JSON/Object/JsonArray.
     */
    @Builder.Default
    @ToString.Exclude
    @Valid
    private List<ParamInfoRequest> childParam = new ArrayList<>();
}
