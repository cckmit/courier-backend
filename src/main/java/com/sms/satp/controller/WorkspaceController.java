package com.sms.satp.controller;

import static com.sms.satp.common.constant.Constants.WORKSPACE_PATH;

import com.sms.satp.common.validate.InsertGroup;
import com.sms.satp.common.validate.UpdateGroup;
import com.sms.satp.dto.request.WorkspaceRequest;
import com.sms.satp.dto.response.WorkspaceResponse;
import com.sms.satp.service.WorkspaceService;
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
@RequestMapping(WORKSPACE_PATH)
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @GetMapping("/{id}")
    public WorkspaceResponse getById(@PathVariable("id") String id) {
        return workspaceService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasRoleOrAdmin(@role.ADMIN)")
    public Boolean add(@Validated(InsertGroup.class) @RequestBody WorkspaceRequest workspaceRequest) {
        return workspaceService.add(workspaceRequest);
    }

    @PutMapping
    @PreAuthorize("hasRoleOrAdmin(@role.ADMIN)")
    public Boolean edit(@Validated(UpdateGroup.class) @RequestBody WorkspaceRequest workspaceRequest) {
        return workspaceService.edit(workspaceRequest);
    }

    @GetMapping("/list")
    @PreAuthorize("hasRoleOrAdmin(@role.ADMIN)")
    public List<WorkspaceResponse> list() {
        return workspaceService.list();
    }

    @GetMapping("/own")
    @PreAuthorize("hasRoleOrAdmin(@role.WORKSPACE_QUERY_OWN)")
    public List<WorkspaceResponse> findByUserId() {
        return workspaceService.findByUserId();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRoleOrAdmin(@role.ADMIN)")
    public Boolean delete(@PathVariable String id) {
        return workspaceService.delete(id);
    }
}