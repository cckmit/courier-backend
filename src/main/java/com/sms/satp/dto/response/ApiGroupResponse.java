package com.sms.satp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ApiGroupResponse extends BaseResponse {

    private String projectId;
    private String name;
    private String parentId;
    private Integer depth;
    private boolean hasNext;
}
