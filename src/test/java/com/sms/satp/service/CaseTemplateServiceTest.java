package com.sms.satp.service;

import com.google.common.collect.Lists;
import com.sms.satp.common.ApiTestPlatformException;
import com.sms.satp.dto.PageDto;
import com.sms.satp.entity.dto.CaseTemplateApiDto;
import com.sms.satp.entity.dto.CaseTemplateDto;
import com.sms.satp.entity.dto.CaseTemplateSearchDto;
import com.sms.satp.entity.scenetest.CaseTemplate;
import com.sms.satp.entity.scenetest.CaseTemplateApi;
import com.sms.satp.mapper.CaseTemplateMapper;
import com.sms.satp.mapper.SceneCaseApiLogMapper;
import com.sms.satp.repository.CaseTemplateRepository;
import com.sms.satp.repository.CustomizedCaseTemplateRepository;
import com.sms.satp.service.impl.CaseTemplateServiceImpl;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static com.sms.satp.common.ErrorCode.ADD_CASE_TEMPLATE_ERROR;
import static com.sms.satp.common.ErrorCode.DELETE_CASE_TEMPLATE_ERROR;
import static com.sms.satp.common.ErrorCode.EDIT_CASE_TEMPLATE_ERROR;
import static com.sms.satp.common.ErrorCode.GET_CASE_TEMPLATE_PAGE_ERROR;
import static com.sms.satp.common.ErrorCode.SEARCH_CASE_TEMPLATE_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Test cases for CaseTemplateServiceTest")
class CaseTemplateServiceTest {

    private CaseTemplateRepository caseTemplateRepository;
    private CustomizedCaseTemplateRepository customizedCaseTemplateRepository;
    private CaseTemplateMapper caseTemplateMapper;
    private CaseTemplateServiceImpl caseTemplateService;
    private CaseTemplateApiService caseTemplateApiService;
    private ApplicationEventPublisher applicationEventPublisher;
    private SceneCaseApiLogMapper sceneCaseApiLogMapper;

    private final static String MOCK_ID = new ObjectId().toString();
    private final static String MOCK_NAME = "test";
    private final static String MOCK_PROJECT_ID = "1";
    private final static String MOCK_GROUP_ID = "1";
    private final static Long MOCK_CREATE_USER_ID = 1L;
    private final static Integer MOCK_PAGE = 1;
    private final static Integer MOCK_SIZE = 1;
    private final static long MOCK_TOTAL = 1L;


    @BeforeEach
    void setBean() {
        caseTemplateRepository = mock(CaseTemplateRepository.class);
        customizedCaseTemplateRepository = mock(CustomizedCaseTemplateRepository.class);
        caseTemplateMapper = mock(CaseTemplateMapper.class);
        caseTemplateApiService = mock(CaseTemplateApiService.class);
        applicationEventPublisher = mock(ApplicationEventPublisher.class);
        sceneCaseApiLogMapper = mock(SceneCaseApiLogMapper.class);
        caseTemplateService = new CaseTemplateServiceImpl(caseTemplateRepository, customizedCaseTemplateRepository,
            caseTemplateMapper, caseTemplateApiService, applicationEventPublisher, sceneCaseApiLogMapper);
    }

    @Test
    @DisplayName("Test the add method in the CaseTemplate service")
    void add_test() {
        CaseTemplate caseTemplate =
            CaseTemplate.builder().name(MOCK_NAME).projectId(MOCK_PROJECT_ID).groupId(MOCK_GROUP_ID)
                .createUserId(MOCK_CREATE_USER_ID).build();
        when(caseTemplateMapper.toAddCaseTemplate(any())).thenReturn(caseTemplate);
        when(caseTemplateRepository.insert(any(CaseTemplate.class))).thenReturn(caseTemplate);
        caseTemplateService.add(CaseTemplateDto.builder().build());
        verify(caseTemplateRepository, times(1)).insert(any(CaseTemplate.class));
    }

    @Test
    @DisplayName("Test the add method in the CaseTemplate service throws exception")
    void add_test_thenThrowException() {
        CaseTemplate caseTemplate =
            CaseTemplate.builder().name(MOCK_NAME).projectId(MOCK_PROJECT_ID).groupId(MOCK_GROUP_ID)
                .createUserId(MOCK_CREATE_USER_ID).build();
        when(caseTemplateMapper.toAddCaseTemplate(any())).thenReturn(caseTemplate);
        when(caseTemplateRepository.insert(any(CaseTemplate.class)))
            .thenThrow(new ApiTestPlatformException(ADD_CASE_TEMPLATE_ERROR));
        assertThatThrownBy(() -> caseTemplateService.add(CaseTemplateDto.builder().build()))
            .isInstanceOf(ApiTestPlatformException.class);
    }

    @Test
    @DisplayName("Test the deleteById method in the CaseTemplate service")
    void deleteById_test() {
        Optional<CaseTemplate> caseTemplate = Optional.ofNullable(CaseTemplate.builder().id(MOCK_ID).build());
        when(caseTemplateRepository.findById(any())).thenReturn(caseTemplate);
        doNothing().when(caseTemplateRepository).deleteById(any());
        List<CaseTemplateApi> caseTemplateApiList = Lists.newArrayList(CaseTemplateApi.builder().build());
        when(caseTemplateApiService.listByCaseTemplateId(any())).thenReturn(caseTemplateApiList);
        doNothing().when(caseTemplateApiService).deleteById(any());
        caseTemplateService.deleteById(MOCK_ID);
        verify(caseTemplateRepository, times(1)).deleteById(any());
    }

    @Test
    @DisplayName("Test the deleteById method in the CaseTemplate service throws exception")
    void deleteById_test_thenThrownException() {
        Optional<CaseTemplate> caseTemplate = Optional.ofNullable(CaseTemplate.builder().id(MOCK_ID).build());
        when(caseTemplateRepository.findById(any())).thenReturn(caseTemplate);
        doThrow(new ApiTestPlatformException(DELETE_CASE_TEMPLATE_ERROR)).when(caseTemplateRepository)
            .deleteById(any());
        assertThatThrownBy(() -> caseTemplateService.deleteById(MOCK_ID))
            .isInstanceOf(ApiTestPlatformException.class);
    }

    @Test
    @DisplayName("Test the edit method in the CaseTemplate service")
    void edit_test() {
        CaseTemplate caseTemplate =
            CaseTemplate.builder().id(MOCK_ID).modifyUserId(MOCK_CREATE_USER_ID).name(MOCK_NAME)
                .remove(Boolean.FALSE).build();
        when(caseTemplateMapper.toUpdateCaseTemplate(any())).thenReturn(caseTemplate);
        Optional<CaseTemplate> optionalSceneCase = Optional
            .ofNullable(CaseTemplate.builder().remove(Boolean.TRUE).build());
        when(caseTemplateRepository.findById(any())).thenReturn(optionalSceneCase);
        when(caseTemplateRepository.save(any(CaseTemplate.class))).thenReturn(caseTemplate);
        List<CaseTemplateApiDto> caseTemplateApiDtoList = Lists
            .newArrayList(CaseTemplateApiDto.builder().id(MOCK_ID).build());
        when(caseTemplateApiService.listByCaseTemplateId(any(), anyBoolean())).thenReturn(caseTemplateApiDtoList);
        doNothing().when(caseTemplateApiService).edit(any());
        caseTemplateService.edit(CaseTemplateDto.builder().remove(Boolean.FALSE).build());
        verify(caseTemplateRepository, times(1)).save(any(CaseTemplate.class));
    }

    @Test
    @DisplayName("Test the edit method in the CaseTemplate service throws exception")
    void edit_test_thenThrownException() {
        CaseTemplate caseTemplate =
            CaseTemplate.builder().id(MOCK_ID).modifyUserId(MOCK_CREATE_USER_ID).name(MOCK_NAME)
                .remove(Boolean.FALSE).build();
        when(caseTemplateMapper.toUpdateCaseTemplate(any())).thenReturn(caseTemplate);
        Optional<CaseTemplate> optionalSceneCase = Optional
            .ofNullable(CaseTemplate.builder().remove(Boolean.TRUE).build());
        when(caseTemplateRepository.findById(any())).thenReturn(optionalSceneCase);
        when(caseTemplateRepository.save(any(CaseTemplate.class)))
            .thenThrow(new ApiTestPlatformException(EDIT_CASE_TEMPLATE_ERROR));
        assertThatThrownBy(() -> caseTemplateService.edit(CaseTemplateDto.builder().build()))
            .isInstanceOf(ApiTestPlatformException.class);
    }

    @Test
    @DisplayName("Test the page method in the CaseTemplate service")
    void page_test() {
        List<CaseTemplate> dtoList = Lists.newArrayList(CaseTemplate.builder().build());
        Pageable pageable = PageRequest.of(MOCK_PAGE, MOCK_SIZE);
        Page<CaseTemplate> caseTemplatePage = new PageImpl<>(dtoList, pageable, MOCK_TOTAL);
        when(caseTemplateRepository.findAll(any(), (Pageable) any())).thenReturn(caseTemplatePage);
        Page<CaseTemplateDto> pageDto = caseTemplateService.page(PageDto.builder().build(), MOCK_PROJECT_ID);
        assertThat(pageDto).isNotNull();
    }

    @Test
    @DisplayName("Test the page method in the CaseTemplate service thrown exception")
    void page_test_thenThrownException() {
        when(caseTemplateRepository.findAll(any(), (Pageable) any()))
            .thenThrow(new ApiTestPlatformException(GET_CASE_TEMPLATE_PAGE_ERROR));
        assertThatThrownBy(() -> caseTemplateService.page(PageDto.builder().build(), MOCK_PROJECT_ID))
            .isInstanceOf(ApiTestPlatformException.class);
    }

    @Test
    @DisplayName("Test the search method in the CaseTemplate service")
    void search_test() {
        CaseTemplateSearchDto dto = new CaseTemplateSearchDto();
        dto.setName(MOCK_NAME);
        List<CaseTemplate> dtoList = Lists.newArrayList(CaseTemplate.builder().build());
        Pageable pageable = PageRequest.of(MOCK_PAGE, MOCK_SIZE);
        Page<CaseTemplate> caseTemplatePage = new PageImpl<>(dtoList, pageable, MOCK_TOTAL);
        when(customizedCaseTemplateRepository.search(any(), any())).thenReturn(caseTemplatePage);
        Page<CaseTemplateDto> pageDto = caseTemplateService.search(dto, MOCK_PROJECT_ID);
        assertThat(pageDto).isNotNull();
    }

    @Test
    @DisplayName("Test the search method in the CaseTemplate service thrown exception")
    void search_test_thenThrownException() {
        CaseTemplateSearchDto dto = new CaseTemplateSearchDto();
        dto.setName(MOCK_NAME);
        when(customizedCaseTemplateRepository.search(any(), any()))
            .thenThrow(new ApiTestPlatformException(SEARCH_CASE_TEMPLATE_ERROR));
        assertThatThrownBy(() -> caseTemplateService.search(dto, MOCK_PROJECT_ID))
            .isInstanceOf(ApiTestPlatformException.class);
    }
}
