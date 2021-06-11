package com.sms.satp.service.impl;

import static com.sms.satp.common.exception.ErrorCode.GET_API_TEST_CASE_JOB_ERROR;
import static com.sms.satp.common.exception.ErrorCode.THE_ENVIRONMENT_NOT_EXITS_ERROR;

import com.sms.satp.common.exception.ApiTestPlatformException;
import com.sms.satp.dto.request.ApiTestCaseJobPageRequest;
import com.sms.satp.dto.request.ApiTestCaseJobRunRequest;
import com.sms.satp.dto.request.DataCollectionRequest;
import com.sms.satp.dto.response.ApiTestCaseJobPageResponse;
import com.sms.satp.dto.response.ApiTestCaseJobResponse;
import com.sms.satp.dto.response.ApiTestCaseResponse;
import com.sms.satp.engine.service.CaseDispatcherService;
import com.sms.satp.entity.env.ProjectEnvironment;
import com.sms.satp.entity.job.ApiTestCaseJob;
import com.sms.satp.entity.job.ApiTestCaseJobReport;
import com.sms.satp.entity.job.JobCaseApi;
import com.sms.satp.entity.job.common.CaseReport;
import com.sms.satp.entity.job.common.JobApiTestCase;
import com.sms.satp.entity.job.common.JobDataCollection;
import com.sms.satp.mapper.JobMapper;
import com.sms.satp.repository.ApiTestCaseJobRepository;
import com.sms.satp.repository.CustomizedApiTestCaseJobRepository;
import com.sms.satp.service.ApiTestCaseJobService;
import com.sms.satp.service.ApiTestCaseService;
import com.sms.satp.service.ProjectEnvironmentService;
import com.sms.satp.utils.ExceptionUtils;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ApiTestCaseJobServiceImpl implements ApiTestCaseJobService {

    private final ApiTestCaseJobRepository apiTestCaseJobRepository;
    private final CustomizedApiTestCaseJobRepository customizedApiTestCaseJobRepository;
    private final CaseDispatcherService caseDispatcherService;
    private final ProjectEnvironmentService projectEnvironmentService;
    private final ApiTestCaseService apiTestCaseService;
    private final JobMapper jobMapper;
    private static final String PREFIX = "/user/";

    public ApiTestCaseJobServiceImpl(ApiTestCaseJobRepository apiTestCaseJobRepository,
        CustomizedApiTestCaseJobRepository customizedApiTestCaseJobRepository,
        CaseDispatcherService caseDispatcherService,
        ProjectEnvironmentService projectEnvironmentService, ApiTestCaseService apiTestCaseService,
        JobMapper jobMapper) {
        this.apiTestCaseJobRepository = apiTestCaseJobRepository;
        this.customizedApiTestCaseJobRepository = customizedApiTestCaseJobRepository;
        this.caseDispatcherService = caseDispatcherService;
        this.projectEnvironmentService = projectEnvironmentService;
        this.apiTestCaseService = apiTestCaseService;
        this.jobMapper = jobMapper;
    }


    @Override
    public void handleJobReport(ApiTestCaseJobReport apiTestCaseJobReport) {
        apiTestCaseJobRepository.findById(apiTestCaseJobReport.getJobId()).ifPresent(job -> {
            log.info("Handle job report. jobReport:{}", apiTestCaseJobReport.toString());
            JobApiTestCase jobApiTestCase = job.getApiTestCase().getJobApiTestCase();
            CaseReport caseReport = apiTestCaseJobReport.getCaseReport();
            jobApiTestCase.setCaseReport(caseReport);
            job.setJobStatus(apiTestCaseJobReport.getJobStatus());
            job.setMessage(apiTestCaseJobReport.getMessage());
            caseDispatcherService.sendJobReport(PREFIX + job.getCreateUserId(), caseReport);
            apiTestCaseJobRepository.save(job);
        });
    }

    @Override
    public void runJob(ApiTestCaseJobRunRequest apiTestCaseJobRunRequest) {
        DataCollectionRequest dataCollectionRequest = apiTestCaseJobRunRequest.getDataCollectionRequest();
        // getCurrentUserId
        int userId = 1;
        try {
            String apiTestCaseId = Objects.requireNonNull(apiTestCaseJobRunRequest.getApiTestCaseId());
            String envId = Objects.requireNonNull(apiTestCaseJobRunRequest.getEnvId());
            ApiTestCaseResponse apiTestCaseResponse = apiTestCaseService.findById(apiTestCaseId);
            ProjectEnvironment projectEnvironment = projectEnvironmentService.findOne(envId);
            if (Objects.isNull(projectEnvironment)) {
                throw ExceptionUtils.mpe(THE_ENVIRONMENT_NOT_EXITS_ERROR);
            }
            ApiTestCaseJob apiTestCaseJob = ApiTestCaseJob.builder()
                .apiTestCase(
                    JobCaseApi.builder().jobApiTestCase(jobMapper.toJobApiTestCase(apiTestCaseResponse)).build())
                .environment(jobMapper.toJobEnvironment(projectEnvironment))
                .build();
            if (Objects.nonNull(dataCollectionRequest) && CollectionUtils
                .isNotEmpty(dataCollectionRequest.getDataList())) {
                JobDataCollection jobDataCollection = jobMapper.toJobDataCollection(dataCollectionRequest);
                dataCollectionRequest.getDataList().forEach(dataList -> {
                    apiTestCaseJob.setId(null);
                    jobDataCollection.setTestData(jobMapper.toTestDataEntity(dataList));
                    apiTestCaseJob.setDataCollection(jobDataCollection);
                    apiTestCaseJobRepository.insert(apiTestCaseJob);
                    caseDispatcherService.dispatch(apiTestCaseJob);
                });
                return;
            }
            apiTestCaseJobRepository.insert(apiTestCaseJob);
            caseDispatcherService.dispatch(apiTestCaseJob);
        } catch (ApiTestPlatformException apiTestPlatEx) {
            log.error(apiTestPlatEx.getMessage());
            caseDispatcherService.sendMessage(PREFIX + userId, apiTestPlatEx.getMessage());
        } catch (Exception e) {
            log.error("Execute the ApiTestCase error. errorMessage:{}", e.getMessage());
            caseDispatcherService.sendMessage(PREFIX + userId, "Execute the ApiTestCase error");
        }
    }

    @Override
    public Page<ApiTestCaseJobPageResponse> page(ApiTestCaseJobPageRequest pageRequest) {
        return customizedApiTestCaseJobRepository.page(pageRequest).map(jobMapper::toApiTestCaseJobPageResponse);
    }

    @Override
    public ApiTestCaseJobResponse get(String jobId) {
        return apiTestCaseJobRepository.findById(jobId).map(jobMapper::toApiTestCaseJobResponse)
            .orElseThrow(() -> ExceptionUtils.mpe(GET_API_TEST_CASE_JOB_ERROR));
    }
}
