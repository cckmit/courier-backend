package com.sms.satp.controller;

import com.sms.satp.common.constant.Constants;
import com.sms.satp.dto.request.AddSceneCaseGroupRequest;
import com.sms.satp.dto.request.SearchSceneCaseGroupRequest;
import com.sms.satp.dto.request.UpdateSceneCaseGroupRequest;
import com.sms.satp.dto.response.SceneCaseGroupResponse;
import com.sms.satp.service.SceneCaseGroupService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.SCENE_CASE_GROUP_PATH)
public class SceneCaseGroupController {

    private final SceneCaseGroupService sceneCaseGroupService;

    public SceneCaseGroupController(SceneCaseGroupService sceneCaseGroupService) {
        this.sceneCaseGroupService = sceneCaseGroupService;
    }

    @PostMapping
    public Boolean add(@Valid @RequestBody AddSceneCaseGroupRequest request) {
        return sceneCaseGroupService.add(request);
    }

    @PutMapping
    public Boolean edit(@Valid @RequestBody UpdateSceneCaseGroupRequest request) {
        return sceneCaseGroupService.edit(request);
    }

    @DeleteMapping(value = "/{id}")
    public Boolean deleteById(@PathVariable String id) {
        return sceneCaseGroupService.deleteById(id);
    }

    @GetMapping
    public List<SceneCaseGroupResponse> getList(SearchSceneCaseGroupRequest request) {
        return sceneCaseGroupService.getList(request);
    }

}