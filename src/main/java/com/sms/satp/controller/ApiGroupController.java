package com.sms.satp.controller;

import com.sms.satp.common.constant.Constants;
import com.sms.satp.common.validate.InsertGroup;
import com.sms.satp.common.validate.UpdateGroup;
import com.sms.satp.dto.request.ApiGroupRequest;
import com.sms.satp.dto.response.ApiGroupResponse;
import com.sms.satp.service.ApiGroupService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.API_GROUP_PATH)
public class ApiGroupController {

    private final ApiGroupService apiGroupService;

    public ApiGroupController(ApiGroupService apiGroupService) {
        this.apiGroupService = apiGroupService;
    }

    @GetMapping(value = "/list/{projectId}")
    @PreAuthorize("hasRoleOrAdmin(@role.API_GROUP_QUERY_ALL)")
    public List<ApiGroupResponse> list(@PathVariable String projectId, String groupId) {
        return apiGroupService.list(projectId, groupId);
    }

    @PostMapping
    @PreAuthorize("hasRoleOrAdmin(@role.API_GROUP_CRE_UPD_DEL)")
    public Boolean add(@Validated(InsertGroup.class) @RequestBody ApiGroupRequest request) {
        return apiGroupService.add(request);
    }

    @PutMapping
    @PreAuthorize("hasRoleOrAdmin(@role.API_GROUP_CRE_UPD_DEL)")
    public Boolean edit(@Validated(UpdateGroup.class) @RequestBody ApiGroupRequest request) {
        return apiGroupService.edit(request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAllRoleOrAdmin(@role.API_GROUP_CRE_UPD_DEL,@role.API_CRE_UPD_DEL)")
    public Boolean delete(@PathVariable String id) {
        return apiGroupService.delete(id);
    }

}
