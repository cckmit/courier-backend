package com.sms.satp.controller;

import com.sms.satp.common.constant.Constants;
import com.sms.satp.dto.PageDto;
import com.sms.satp.dto.request.AddCaseTemplateRequest;
import com.sms.satp.dto.request.CaseTemplateSearchRequest;
import com.sms.satp.dto.request.UpdateCaseTemplateRequest;
import com.sms.satp.dto.response.CaseTemplateResponse;
import com.sms.satp.service.CaseTemplateService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.CASE_TEMPLATE_PATH)
public class CaseTemplateController {

    private final CaseTemplateService caseTemplateService;

    public CaseTemplateController(CaseTemplateService caseTemplateService) {
        this.caseTemplateService = caseTemplateService;
    }

    @PostMapping
    public Boolean add(@Valid @RequestBody AddCaseTemplateRequest addCaseTemplateRequest) {
        return caseTemplateService.add(addCaseTemplateRequest);
    }

    @DeleteMapping("/{ids}")
    public Boolean deleteByIds(@PathVariable List<String> ids) {
        return caseTemplateService.deleteByIds(ids);
    }

    @PutMapping
    public Boolean edit(@Valid @RequestBody UpdateCaseTemplateRequest updateCaseTemplateRequest) {
        return caseTemplateService.edit(updateCaseTemplateRequest);
    }

    @GetMapping("/page/{projectId}")
    public Page<CaseTemplateResponse> page(PageDto pageDto, @PathVariable String projectId) {
        return caseTemplateService.page(pageDto, projectId);
    }

    @GetMapping("/search/{projectId}")
    public Page<CaseTemplateResponse> search(CaseTemplateSearchRequest searchDto,
        @PathVariable String projectId) {
        return caseTemplateService.search(searchDto, projectId);
    }

}