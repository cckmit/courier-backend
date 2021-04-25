package com.sms.satp.entity;

import com.sms.satp.common.enums.RequestMethod;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "InterfaceHistory")
public class InterfaceHistory {

    @Id
    @Field("_id")
    private String id;
    private RequestMethod method;
    private List<String> tag;
    private String md5;
    private String title;
    private String path;
    private String description;
    @Field("group_id")
    private String groupId;
    @Field("project_id")
    private String projectId;
    @Field("request_headers")
    private List<Header> requestHeaders;
    @Field("response_headers")
    private List<Header> responseHeaders;
    @Field("query_params")
    private List<Parameter> queryParams;
    @Field("path_params")
    private List<Parameter> pathParams;
    @Field("request_body")
    private RequestBody requestBody;
    private Response response;
    @Field("create_date_time")
    private LocalDateTime createDateTime;
    @Field("modify_date_time")
    private LocalDateTime modifyDateTime;

}