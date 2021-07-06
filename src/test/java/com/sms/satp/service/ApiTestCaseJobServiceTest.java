package com.sms.satp.service;

import static com.sms.satp.common.exception.ErrorCode.GET_API_TEST_CASE_JOB_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sms.satp.common.exception.ApiTestPlatformException;
import com.sms.satp.dto.request.ApiTestCaseJobPageRequest;
import com.sms.satp.dto.request.ApiTestCaseJobRunRequest;
import com.sms.satp.dto.request.DataCollectionRequest;
import com.sms.satp.dto.request.DataParamRequest;
import com.sms.satp.dto.request.TestDataRequest;
import com.sms.satp.dto.response.ApiTestCaseJobResponse;
import com.sms.satp.dto.response.ApiTestCaseResponse;
import com.sms.satp.engine.service.CaseDispatcherService;
import com.sms.satp.entity.env.ProjectEnvironment;
import com.sms.satp.entity.job.ApiTestCaseJob;
import com.sms.satp.entity.job.ApiTestCaseJobReport;
import com.sms.satp.entity.job.JobCaseApi;
import com.sms.satp.entity.job.common.CaseReport;
import com.sms.satp.entity.job.common.JobApiTestCase;
import com.sms.satp.mapper.JobMapper;
import com.sms.satp.mapper.JobMapperImpl;
import com.sms.satp.mapper.ParamInfoMapperImpl;
import com.sms.satp.repository.ApiTestCaseJobRepository;
import com.sms.satp.repository.CustomizedApiTestCaseJobRepository;
import com.sms.satp.service.impl.ApiTestCaseJobServiceImpl;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;

@DisplayName("Tests for ApiTestCaseJobJobService")
class ApiTestCaseJobServiceTest {

    private final ApiTestCaseJobRepository apiTestCaseJobRepository = mock(ApiTestCaseJobRepository.class);
    private final CaseDispatcherService caseDispatcherService = mock(CaseDispatcherService.class);
    private final ProjectEnvironmentService projectEnvironmentService = mock(ProjectEnvironmentService.class);
    private final ApiTestCaseService apiTestCaseService = mock(ApiTestCaseService.class);
    private final CustomizedApiTestCaseJobRepository customizedApiTestCaseJobRepository = mock(
        CustomizedApiTestCaseJobRepository.class);
    private final JobMapper jobMapper = new JobMapperImpl(new ParamInfoMapperImpl());
    private final ApiTestCaseJobService apiTestCaseJobService = new ApiTestCaseJobServiceImpl(
        apiTestCaseJobRepository, customizedApiTestCaseJobRepository, caseDispatcherService, projectEnvironmentService
        , apiTestCaseService, jobMapper);
    private final ApiTestCaseJob apiTestCaseJob =
        ApiTestCaseJob.builder().id(ID)
            .apiTestCase(JobCaseApi.builder().jobApiTestCase(JobApiTestCase.builder().build()).build()).build();
    private final TestDataRequest testDataRequest =
        TestDataRequest.builder().dataName("test")
            .data(List.of(DataParamRequest.builder().key("key").value("value").build())).build();
    private final ApiTestCaseJobRunRequest apiTestCaseJobRunRequest =
        ApiTestCaseJobRunRequest.builder().apiTestCaseId(ObjectId.get().toString()).envId(ObjectId.get().toString())
            .dataCollectionRequest(
                DataCollectionRequest.builder().collectionName("test")
                    .dataList(Collections.singletonList(testDataRequest)).build()).build();
    private final ApiTestCaseJobRunRequest apiTestCaseJobRunRequest2 =
        ApiTestCaseJobRunRequest.builder().apiTestCaseId(ObjectId.get().toString()).envId(ObjectId.get().toString())
            .build();
    private final ApiTestCaseResponse apiTestCaseResponse = ApiTestCaseResponse.builder()
        .id(ID).build();
    private final ProjectEnvironment projectEnvironment = ProjectEnvironment.builder().build();
    private static final String ID = ObjectId.get().toString();

    @Test
    @DisplayName("Test the findById method in the ApiTestCaseJob service")
    public void findById_test() {
        when(apiTestCaseJobRepository.findById(ID)).thenReturn(Optional.of(apiTestCaseJob));
        ApiTestCaseJobResponse apiTestCaseJobResponse = apiTestCaseJobService.get(ID);
        assertThat(apiTestCaseJobResponse).isNotNull();
        assertThat(apiTestCaseJobResponse.getId()).isEqualTo(ID);
    }

    @Test
    @DisplayName("An exception occurred while getting ApiTestCaseJob")
    public void findById_exception_test() {
        when(apiTestCaseJobRepository.findById(ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> apiTestCaseJobService.get(ID)).isInstanceOf(ApiTestPlatformException.class)
            .extracting("code").isEqualTo(GET_API_TEST_CASE_JOB_ERROR.getCode());
    }

    @Test
    @DisplayName("Test the page method in the ApiTestCaseJob service")
    public void page_test() {
        when(customizedApiTestCaseJobRepository.page(any())).thenReturn(new PageImpl<>(Collections.emptyList()));
        assertThat(apiTestCaseJobService.page(new ApiTestCaseJobPageRequest())).isNotNull();
    }

    @Test
    @DisplayName("Test the handleJobReport method in the ApiTestCaseJob service")
    public void handleJobReport_test() {
        when(apiTestCaseJobRepository.findById(any())).thenReturn(Optional.of(apiTestCaseJob));
        when(apiTestCaseJobRepository.save(any(ApiTestCaseJob.class))).thenReturn(apiTestCaseJob);
        doNothing().when(caseDispatcherService).sendJobReport(anyString(), any(CaseReport.class));
        doNothing().when(caseDispatcherService).dispatch(any(ApiTestCaseJob.class));
        apiTestCaseJobService.handleJobReport(ApiTestCaseJobReport.builder().jobId(ObjectId.get().toString()).build());
        verify(apiTestCaseJobRepository, times(1)).save(any(ApiTestCaseJob.class));
    }

    @Test
    @DisplayName("Test the runJob method in the ApiTestCaseJob service")
    public void runJob_test() {
        when(apiTestCaseService.findById(any())).thenReturn(apiTestCaseResponse);
        when(projectEnvironmentService.findOne(any())).thenReturn(projectEnvironment);
        when(apiTestCaseJobRepository.insert(any(ApiTestCaseJob.class))).thenReturn(apiTestCaseJob);
        doNothing().when(caseDispatcherService).dispatch(any(ApiTestCaseJob.class));
        apiTestCaseJobService.runJob(apiTestCaseJobRunRequest);
        verify(apiTestCaseJobRepository, times(1)).insert(any(ApiTestCaseJob.class));
    }

    @Test
    @DisplayName("Test the runJob method in the ApiTestCaseJob service")
    public void runJob2_test() {
        when(apiTestCaseService.findById(any())).thenReturn(apiTestCaseResponse);
        when(projectEnvironmentService.findOne(any())).thenReturn(projectEnvironment);
        when(apiTestCaseJobRepository.insert(any(ApiTestCaseJob.class))).thenReturn(apiTestCaseJob);
        doNothing().when(caseDispatcherService).dispatch(any(ApiTestCaseJob.class));
        apiTestCaseJobService.runJob(apiTestCaseJobRunRequest2);
        verify(apiTestCaseJobRepository, times(1)).insert(any(ApiTestCaseJob.class));
    }

    @Test
    @DisplayName("An exception occurred while execute ApiTestCaseJob")
    public void environment_not_exist_exception_test() {
        when(apiTestCaseService.findById(any())).thenReturn(apiTestCaseResponse);
        when(projectEnvironmentService.findOne(any())).thenReturn(null);
        apiTestCaseJobService.runJob(apiTestCaseJobRunRequest);
        doNothing().when(caseDispatcherService).sendMessage(anyString(), anyString());
        verify(caseDispatcherService, times(1)).sendMessage(anyString(), anyString());
    }

    @Test
    @DisplayName("An exception occurred while execute ApiTestCaseJob")
    public void execute_exception_test() {
        when(apiTestCaseService.findById(any())).thenReturn(apiTestCaseResponse);
        when(projectEnvironmentService.findOne(any())).thenThrow(new RuntimeException());
        apiTestCaseJobService.runJob(apiTestCaseJobRunRequest);
        doNothing().when(caseDispatcherService).sendJobReport(anyString(), any(CaseReport.class));
        doNothing().when(caseDispatcherService).sendMessage(anyString(), anyString());
        verify(caseDispatcherService, times(1)).sendMessage(anyString(), anyString());
    }

}