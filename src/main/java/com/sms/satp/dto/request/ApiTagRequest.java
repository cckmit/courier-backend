package com.sms.satp.dto.request;

import com.sms.satp.common.validate.InsertGroup;
import com.sms.satp.common.validate.UpdateGroup;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ApiTagRequest {

    @NotEmpty(groups = UpdateGroup.class, message = "The id cannot be empty.")
    private String id;

    @NotEmpty(groups = {InsertGroup.class, UpdateGroup.class}, message = "The projectId cannot be empty.")
    private String projectId;

    private String groupId;

    @NotEmpty(groups = {InsertGroup.class, UpdateGroup.class}, message = "The tagName cannot be empty.")
    private String tagName;

    @NotNull(groups = {InsertGroup.class, UpdateGroup.class}, message = "The tagType cannot by null.")
    @Range(min = 1, max = 3, groups = {InsertGroup.class,
        UpdateGroup.class}, message = "The tayType must between 1 and 3.")
    private Integer tagType;
}
