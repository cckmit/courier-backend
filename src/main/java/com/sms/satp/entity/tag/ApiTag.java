package com.sms.satp.entity.tag;

import com.sms.satp.common.enums.ApiTagType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Document(collection = "ApiTag")
public class ApiTag {

    @MongoId(FieldType.OBJECT_ID)
    private String id;
    @Field(targetType = FieldType.OBJECT_ID)
    private String projectId;
    @Field(targetType = FieldType.OBJECT_ID)
    private String groupId;
    private String tagName;
    private ApiTagType tagType;
    @CreatedBy
    private Long createUserId;
    @LastModifiedBy
    private Long modifyUserId;
    @CreatedDate
    private LocalDateTime createDateTime;
    @LastModifiedDate
    private LocalDateTime modifyDateTime;
}