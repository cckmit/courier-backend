package com.sms.courier.engine.impl;

import com.sms.courier.common.exception.ApiTestPlatformException;
import com.sms.courier.dto.request.CaseRecordRequest;
import com.sms.courier.engine.EngineId;
import com.sms.courier.engine.EngineMemberManagement;
import com.sms.courier.engine.enums.EngineStatus;
import com.sms.courier.engine.model.EngineMemberEntity;
import com.sms.courier.engine.request.EngineRegistrationRequest;
import com.sms.courier.engine.task.SuspiciousEngineManagement;
import com.sms.courier.repository.EngineMemberRepository;
import com.sms.courier.utils.ExceptionUtils;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class EngineMemberManagementImpl implements EngineMemberManagement {

    private final SecureRandom random = new SecureRandom();
    private final EngineMemberRepository engineMemberRepository;
    private final SuspiciousEngineManagement suspiciousEngineManagement;

    public EngineMemberManagementImpl(EngineMemberRepository engineMemberRepository,
        SuspiciousEngineManagement suspiciousEngineManagement) {
        this.engineMemberRepository = engineMemberRepository;
        this.suspiciousEngineManagement = suspiciousEngineManagement;
    }

    @Override
    public String bind(EngineRegistrationRequest request) {
        EngineMemberEntity engineMember =
            EngineMemberEntity.builder().destination(EngineId.generate()).host(request.getHost()).status(
                    EngineStatus.PENDING)
                .version(request.getVersion())
                .build();
        engineMemberRepository.save(engineMember);
        log.info("The destination {} of the test engine is binding.", engineMember.getDestination());
        return engineMember.getDestination();
    }


    @Override
    public String getAvailableMember() throws ApiTestPlatformException {
        List<String> availableMembers = engineMemberRepository.findAllByStatus(EngineStatus.RUNNING)
            .map(EngineMemberEntity::getDestination).collect(
                Collectors.toUnmodifiableList());
        if (CollectionUtils.isEmpty(availableMembers)) {
            throw ExceptionUtils.mpe("No engines are available.");
        }
        return availableMembers.get(random.nextInt(availableMembers.size()));
    }

    @Override
    public void caseRecord(CaseRecordRequest caseRecordRequest) {
        Optional<EngineMemberEntity> engineMemberOptional = engineMemberRepository
            .findFirstByDestination(caseRecordRequest.getDestination());
        engineMemberOptional.ifPresent(engineMember -> {
            engineMember.setCaseTaskSize(caseRecordRequest.getCaseCount());
            engineMember.setSceneCaseTaskSize(caseRecordRequest.getSceneCaseCount());
            engineMember.setCurrentTaskSize(caseRecordRequest.getCaseCount() + caseRecordRequest.getSceneCaseCount());
            engineMemberRepository.save(engineMember);
            log.info("The destination {} currentTask {} caseTask {} sceneCaseTask {}.", engineMember.getDestination(),
                engineMember.getCurrentTaskSize(), engineMember.getCaseTaskSize(), engineMember.getSceneCaseTaskSize());
        });
    }

    @Override
    public void unBind(String sessionId) {
        Optional<EngineMemberEntity> engineMemberOptional = engineMemberRepository.findFirstBySessionId(sessionId);
        engineMemberOptional.ifPresent((engineMember -> {
            engineMember.setStatus(EngineStatus.WAITING_FOR_RECONNECTION);
            engineMemberRepository.save(engineMember);
            suspiciousEngineManagement.add(engineMember.getDestination());
            log.info("The destination {} unbind from member of the engine.", engineMember.getDestination());
        }));
    }

    @Override
    public void active(String sessionId, String destination) {
        Optional<EngineMemberEntity> engineMemberOptional = engineMemberRepository.findFirstByDestination(destination);
        engineMemberOptional.ifPresent(engineMember -> {
            if (engineMember.getStatus() == EngineStatus.WAITING_FOR_RECONNECTION) {
                suspiciousEngineManagement.remove(engineMember.getDestination());
                log.info("The Engine reconnection.destination:{}", engineMember.getDestination());
            }
            engineMember.setStatus(EngineStatus.RUNNING);
            engineMember.setDestination(destination);
            engineMember.setSessionId(sessionId);
            engineMemberRepository.save(engineMember);
            log.info("The test engine {} activated.", destination);
        });
    }

}