package com.sms.courier.common.listener;

import static com.sms.courier.engine.enums.EngineStatus.INVALID;

import com.sms.courier.common.listener.event.EngineInvalidEvent;
import com.sms.courier.engine.model.EngineMemberEntity;
import com.sms.courier.repository.EngineMemberRepository;
import com.sms.courier.service.ApiTestCaseJobService;
import com.sms.courier.service.SceneCaseJobService;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EngineInvalidListener {

    private final EngineMemberRepository engineMemberRepository;
    private final ApiTestCaseJobService apiTestCaseJobService;
    private final SceneCaseJobService sceneCaseJobService;

    public EngineInvalidListener(EngineMemberRepository engineMemberRepository,
        ApiTestCaseJobService apiTestCaseJobService, SceneCaseJobService sceneCaseJobService) {
        this.engineMemberRepository = engineMemberRepository;
        this.apiTestCaseJobService = apiTestCaseJobService;
        this.sceneCaseJobService = sceneCaseJobService;
    }


    @EventListener
    @Async
    public void doProcess(EngineInvalidEvent event) {
        List<String> engineIds = event.getEngineIds();
        if (CollectionUtils.isEmpty(engineIds)) {
            return;
        }
        updateEngineStatus(engineIds);
        // Reallocate api test job to engine.
        apiTestCaseJobService.reallocateJob(engineIds);
        // Reallocate scene job to engine.
        sceneCaseJobService.reallocateJob(engineIds);
    }

    private void updateEngineStatus(List<String> engineIds) {
        List<EngineMemberEntity> engineMemberEntities = engineMemberRepository.findAllByDestinationIn(engineIds);
        engineMemberEntities.forEach(entity -> entity.setStatus(INVALID));
        engineMemberRepository.saveAll(engineMemberEntities);
    }
}