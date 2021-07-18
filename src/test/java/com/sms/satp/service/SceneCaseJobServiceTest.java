package com.sms.satp.service;

import com.sms.satp.common.enums.JobStatus;
import com.sms.satp.common.exception.ApiTestPlatformException;
import com.sms.satp.dto.request.AddSceneCaseJobRequest;
import com.sms.satp.dto.request.DataCollectionRequest;
import com.sms.satp.dto.request.SceneCaseJobRequest;
import com.sms.satp.dto.request.TestDataRequest;
import com.sms.satp.dto.response.SceneCaseJobResponse;
import com.sms.satp.engine.service.CaseDispatcherService;
import com.sms.satp.entity.apitestcase.ApiTestCase;
import com.sms.satp.entity.env.ProjectEnvironment;
import com.sms.satp.entity.job.JobSceneCaseApi;
import com.sms.satp.entity.job.SceneCaseJob;
import com.sms.satp.entity.job.SceneCaseJobReport;
import com.sms.satp.entity.job.common.CaseReport;
import com.sms.satp.entity.job.common.JobApiTestCase;
import com.sms.satp.entity.job.common.JobDataCollection;
import com.sms.satp.entity.scenetest.CaseTemplate;
import com.sms.satp.entity.scenetest.CaseTemplateApi;
import com.sms.satp.entity.scenetest.SceneCase;
import com.sms.satp.entity.scenetest.SceneCaseApi;
import com.sms.satp.mapper.JobMapper;
import com.sms.satp.repository.CaseTemplateApiRepository;
import com.sms.satp.repository.CaseTemplateRepository;
import com.sms.satp.repository.CustomizedCaseTemplateApiRepository;
import com.sms.satp.repository.CustomizedSceneCaseJobRepository;
import com.sms.satp.repository.SceneCaseApiRepository;
import com.sms.satp.repository.SceneCaseJobRepository;
import com.sms.satp.repository.SceneCaseRepository;
import com.sms.satp.service.impl.SceneCaseJobServiceImpl;
import java.util.List;
import java.util.Optional;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static com.sms.satp.common.exception.ErrorCode.GET_SCENE_CASE_JOB_ERROR;
import static com.sms.satp.common.exception.ErrorCode.GET_SCENE_CASE_JOB_PAGE_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Test cases for SceneCaseJobServiceTest")
class SceneCaseJobServiceTest {

    private final SceneCaseApiRepository sceneCaseApiRepository = mock(SceneCaseApiRepository.class);
    private final ProjectEnvironmentService projectEnvironmentService = mock(ProjectEnvironmentService.class);
    private final SceneCaseRepository sceneCaseRepository = mock(SceneCaseRepository.class);
    private final SceneCaseJobRepository sceneCaseJobRepository = mock(SceneCaseJobRepository.class);
    private final JobMapper jobMapper = mock(JobMapper.class);
    private final CustomizedSceneCaseJobRepository customizedSceneCaseJobRepository =
        mock(CustomizedSceneCaseJobRepository.class);
    private final CaseDispatcherService caseDispatcherService = mock(CaseDispatcherService.class);
    private final CustomizedCaseTemplateApiRepository customizedCaseTemplateApiRepository =
        mock(CustomizedCaseTemplateApiRepository.class);

    private final CaseTemplateRepository caseTemplateRepository = mock(CaseTemplateRepository.class);
    private final CaseTemplateApiRepository caseTemplateApiRepository = mock(CaseTemplateApiRepository.class);
    private final SceneCaseJobService sceneCaseJobService = new SceneCaseJobServiceImpl(sceneCaseApiRepository,
        projectEnvironmentService,
        sceneCaseRepository,
        sceneCaseJobRepository,
        jobMapper,
        customizedSceneCaseJobRepository,
        caseDispatcherService,
        customizedCaseTemplateApiRepository,
        caseTemplateRepository,
        caseTemplateApiRepository);

    private final static String MOCK_ID = "1";
    private final static Integer MOCK_NUM = 1;

    @Test
    @DisplayName("Test the runJob method in the SceneCaseJob service")
    void runJob_test() {
        ProjectEnvironment environment = ProjectEnvironment.builder().build();
        when(projectEnvironmentService.findOne(any())).thenReturn(environment);
        Optional<SceneCase> sceneCase = Optional.ofNullable(SceneCase.builder().build());
        when(sceneCaseRepository.findById(any())).thenReturn(sceneCase);
        Optional<CaseTemplate> sceneCaseApi = Optional.ofNullable(CaseTemplate.builder().build());
        when(caseTemplateRepository.findById(any())).thenReturn(sceneCaseApi);
        List<SceneCaseApi> sceneCaseApiList1 = getSceneCaseApiList();
        when(sceneCaseApiRepository.findAllBySceneCaseId(any())).thenReturn(sceneCaseApiList1);
        List<CaseTemplateApi> templateApiList =
            Lists.newArrayList(CaseTemplateApi.builder().id(MOCK_ID).order(MOCK_NUM).build());
        when(caseTemplateApiRepository.findAllByCaseTemplateIdOrderByOrder(any())).thenReturn(templateApiList);
        when(jobMapper.toJobSceneCaseApi(any()))
            .thenReturn(JobSceneCaseApi.builder().id(MOCK_ID).order(MOCK_NUM).build());
        JobSceneCaseApi jobSceneCaseApiList =
            JobSceneCaseApi.builder().id(MOCK_ID).order(MOCK_NUM).build();
        when(jobMapper.toJobSceneCaseApiByTemplate(any())).thenReturn(jobSceneCaseApiList);
        List<CaseTemplateApi> caseTemplateApiList =
            Lists.newArrayList(CaseTemplateApi.builder().id(MOCK_ID).order(MOCK_NUM).build());
        when(customizedCaseTemplateApiRepository.findByCaseTemplateIds(any())).thenReturn(caseTemplateApiList);
        List<JobSceneCaseApi> caseApiList = Lists
            .newArrayList(JobSceneCaseApi.builder().id(MOCK_ID).order(MOCK_NUM).build());
        when(jobMapper.toJobSceneCaseApiListByTemplate(any())).thenReturn(caseApiList);

        JobDataCollection dataCollection1 = JobDataCollection.builder().build();
        when(jobMapper.toJobDataCollection(any())).thenReturn(dataCollection1);
        SceneCaseJob sceneCaseJob = SceneCaseJob.builder().id(MOCK_ID).build();
        when(sceneCaseJobRepository.insert(any(SceneCaseJob.class))).thenReturn(sceneCaseJob);
        AddSceneCaseJobRequest request = getAddRequest();
        sceneCaseJobService.runJob(request);
        verify(sceneCaseJobRepository, times(1)).insert(any(SceneCaseJob.class));
    }

    @Test
    @DisplayName("Test the runJob method in the SceneCaseJob service")
    void runJob_test_DataCollectionIsNull() {
        ProjectEnvironment environment = ProjectEnvironment.builder().build();
        when(projectEnvironmentService.findOne(any())).thenReturn(environment);
        Optional<SceneCase> sceneCase = Optional.ofNullable(SceneCase.builder().build());
        when(sceneCaseRepository.findById(any())).thenReturn(sceneCase);
        SceneCaseJob sceneCaseJob = SceneCaseJob.builder().id(MOCK_ID).build();
        when(sceneCaseJobRepository.insert(any(SceneCaseJob.class))).thenReturn(sceneCaseJob);
        AddSceneCaseJobRequest request = getAddRequest();
        request.setDataCollectionRequest(null);
        sceneCaseJobService.runJob(request);
        verify(sceneCaseJobRepository, times(1)).insert(any(SceneCaseJob.class));
    }

    @Test
    @DisplayName("Test the runJob method in the SceneCaseJob service thrown exception")
    void runJob_test_EnvironmentIsNull() {
        when(projectEnvironmentService.findOne(any())).thenReturn(null);
        doNothing().when(caseDispatcherService).sendErrorMessage(any(), any());
        sceneCaseJobService.runJob(getAddRequest());
        verify(projectEnvironmentService, times(1)).findOne(any());
    }

    @Test
    @DisplayName("Test the runJob method in the SceneCaseJob service thrown exception")
    void runJob_test_SceneCaseIsNull() {
        ProjectEnvironment environment = ProjectEnvironment.builder().build();
        when(projectEnvironmentService.findOne(any())).thenReturn(environment);
        when(sceneCaseRepository.findById(any())).thenReturn(Optional.empty());
        doNothing().when(caseDispatcherService).sendErrorMessage(any(), any());
        sceneCaseJobService.runJob(getAddRequest());
        verify(sceneCaseRepository, times(1)).findById(any());
    }

    @Test
    @DisplayName("An exception occurred while execute SceneCaseJob")
    public void environment_not_exist_exception_test() {
        when(projectEnvironmentService.findOne(any())).thenReturn(null);
        sceneCaseJobService.runJob(getAddRequest());
        doNothing().when(caseDispatcherService).sendErrorMessage(anyString(), anyString());
        verify(caseDispatcherService, times(1)).sendErrorMessage(anyString(), anyString());
    }

    @Test
    @DisplayName("An exception occurred while execute SceneCaseJob")
    public void execute_exception_test() {
        when(projectEnvironmentService.findOne(any())).thenThrow(new RuntimeException());
        sceneCaseJobService.runJob(getAddRequest());
        doNothing().when(caseDispatcherService).sendJobReport(anyString(), any(CaseReport.class));
        doNothing().when(caseDispatcherService).sendErrorMessage(anyString(), anyString());
        verify(caseDispatcherService, times(1)).sendErrorMessage(anyString(), anyString());
    }

    @Test
    @DisplayName("Test the handleJobReport method in the SceneCaseJob service")
    void handleJobReport_test() {
        Optional<SceneCaseJob> sceneCaseJob =
            Optional.ofNullable(
                SceneCaseJob.builder()
                    .apiTestCase(Lists.newArrayList(JobSceneCaseApi.builder().id(MOCK_ID)
                        .jobApiTestCase(JobApiTestCase
                            .builder().id(MOCK_ID).build()).build()))
                    .id(MOCK_ID).build());
        when(sceneCaseJobRepository.findById(any())).thenReturn(sceneCaseJob);
        doNothing().when(caseDispatcherService).sendErrorMessage(any(), any());
        when(sceneCaseJobRepository.save(any())).thenReturn(SceneCaseJob.builder().id(MOCK_ID).build());
        SceneCaseJobReport sceneCaseJobReport = getReport();
        sceneCaseJobService.handleJobReport(sceneCaseJobReport);
        verify(sceneCaseJobRepository, times(1)).save(any());
    }

    private SceneCaseJobReport getReport() {
        return SceneCaseJobReport.builder()
            .jobId(MOCK_ID)
            .jobStatus(JobStatus.SUCCESS)
            .caseReportList(Lists.newArrayList(CaseReport.builder().caseId(MOCK_ID).build()))
            .build();
    }

    @Test
    @DisplayName("Test the page method in the SceneCaseJob service")
    void page_test() {
        Page<SceneCaseJob> sceneCaseJobPage = Page.empty(Pageable.unpaged());
        when(customizedSceneCaseJobRepository.page(any())).thenReturn(sceneCaseJobPage);
        SceneCaseJobRequest request =
            SceneCaseJobRequest.builder().sceneCaseId(MOCK_ID).userIds(Lists.newArrayList(MOCK_ID)).build();
        Page<SceneCaseJobResponse> responsePage = sceneCaseJobService.page(request);
        assertThat(responsePage).isNotNull();
    }

    @Test
    @DisplayName("Test the page method in the SceneCaseJob service thrown exception")
    void page_test_thrownException() {
        when(customizedSceneCaseJobRepository.page(any()))
            .thenThrow(new ApiTestPlatformException(GET_SCENE_CASE_JOB_PAGE_ERROR));
        SceneCaseJobRequest request =
            SceneCaseJobRequest.builder().sceneCaseId(MOCK_ID).userIds(Lists.newArrayList(MOCK_ID)).build();
        assertThatThrownBy(() -> sceneCaseJobService.page(request));
    }

    @Test
    @DisplayName("Test the get method in the SceneCaseJob service")
    void get_test() {
        Optional<SceneCaseJob> sceneCaseJob = Optional.ofNullable(SceneCaseJob.builder().id(MOCK_ID).build());
        when(sceneCaseJobRepository.findById(any())).thenReturn(sceneCaseJob);
        SceneCaseJobResponse response = SceneCaseJobResponse.builder().id(MOCK_ID).build();
        when(jobMapper.toSceneCaseJobResponse(any())).thenReturn(response);
        SceneCaseJobResponse dto = sceneCaseJobService.get(MOCK_ID);
        assertThat(dto).isNotNull();
    }

    @Test
    @DisplayName("Test the get method in the SceneCaseJob service thrown exception")
    void get_test_thrownException() {
        when(sceneCaseJobRepository.findOne(any())).thenThrow(new ApiTestPlatformException(GET_SCENE_CASE_JOB_ERROR));
        assertThatThrownBy(() -> sceneCaseJobService.get(MOCK_ID)).isInstanceOf(ApiTestPlatformException.class);
    }

    private AddSceneCaseJobRequest getAddRequest() {
        return AddSceneCaseJobRequest.builder()
            .sceneCaseId(MOCK_ID)
            .caseTemplateId(MOCK_ID)
            .envId(MOCK_ID)
            .dataCollectionRequest(
                DataCollectionRequest.builder().id(MOCK_ID)
                    .dataList(Lists.newArrayList(TestDataRequest.builder().build())).build())
            .build();
    }

    private List<SceneCaseApi> getSceneCaseApiList() {
        return Lists.newArrayList(
            SceneCaseApi.builder()
                .id(MOCK_ID)
                .order(MOCK_NUM)
                .apiTestCase(ApiTestCase.builder().id(MOCK_ID).execute(Boolean.TRUE).build())
                .build(),
            SceneCaseApi.builder()
                .caseTemplateId(MOCK_ID)
                .order(MOCK_NUM)
                .apiTestCase(ApiTestCase.builder().id(MOCK_ID).execute(Boolean.TRUE).build())
                .build());
    }
}
