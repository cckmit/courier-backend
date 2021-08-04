package com.sms.courier.service.impl;

import static com.sms.courier.common.enums.OperationModule.SCHEDULE;
import static com.sms.courier.common.enums.ScheduleStatusType.CREATE;
import static com.sms.courier.common.enums.ScheduleStatusType.UPDATE;
import static com.sms.courier.common.exception.ErrorCode.ADD_SCHEDULE_ERROR;
import static com.sms.courier.common.exception.ErrorCode.EDIT_NOT_EXIST_ERROR;
import static com.sms.courier.common.exception.ErrorCode.EDIT_SCHEDULE_ERROR;
import static com.sms.courier.common.exception.ErrorCode.GET_SCHEDULE_BY_ID_ERROR;
import static com.sms.courier.common.exception.ErrorCode.GET_SCHEDULE_LIST_ERROR;
import static com.sms.courier.common.field.CommonField.REMOVE;

import com.sms.courier.common.exception.ApiTestPlatformException;
import com.sms.courier.dto.request.ScheduleListRequest;
import com.sms.courier.dto.request.ScheduleRequest;
import com.sms.courier.dto.response.ScheduleResponse;
import com.sms.courier.entity.schedule.ScheduleEntity;
import com.sms.courier.mapper.ScheduleMapper;
import com.sms.courier.repository.CommonRepository;
import com.sms.courier.repository.ScheduleRepository;
import com.sms.courier.service.ScheduleService;
import com.sms.courier.utils.ExceptionUtils;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final CommonRepository commonRepository;
    private final ScheduleMapper scheduleMapper;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository,
        CommonRepository commonRepository, ScheduleMapper scheduleMapper) {
        this.scheduleRepository = scheduleRepository;
        this.commonRepository = commonRepository;
        this.scheduleMapper = scheduleMapper;
    }

    @Override
    public ScheduleResponse findById(String id) {
        return scheduleRepository.findById(id).map(scheduleMapper::toResponse)
            .orElseThrow(() -> ExceptionUtils.mpe(GET_SCHEDULE_BY_ID_ERROR));
    }

    @Override
    public List<ScheduleResponse> list(ScheduleListRequest request) {
        try {
            return commonRepository.listLookupUser(SCHEDULE.getCollectionName(), List.of(REMOVE.is(true)),
                ScheduleResponse.class);
        } catch (Exception e) {
            log.error("Failed to get Schedule list.");
            throw ExceptionUtils.mpe(GET_SCHEDULE_LIST_ERROR);
        }
    }

    @Override
    public Boolean add(ScheduleRequest request) {
        try {
            ScheduleEntity schedule = scheduleMapper.toEntity(request);
            schedule.setScheduleStatus(CREATE);
            scheduleRepository.insert(schedule);
        } catch (Exception e) {
            log.error("Failed to add Schedule. message:{}", e.getMessage());
            throw ExceptionUtils.mpe(ADD_SCHEDULE_ERROR);
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean edit(ScheduleRequest request) {
        try {
            ScheduleEntity oldSchedule = scheduleRepository.findById(request.getId())
                .orElseThrow(() -> ExceptionUtils.mpe(EDIT_NOT_EXIST_ERROR, "Schedule", request.getId()));
            ScheduleEntity schedule = scheduleMapper.toEntity(request);
            if (checkScheduleTime(oldSchedule, schedule)) {
                schedule.setScheduleStatus(UPDATE);
            }
            scheduleRepository.save(schedule);
        } catch (ApiTestPlatformException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to add Schedule. message:{}", e.getMessage());
            throw ExceptionUtils.mpe(EDIT_SCHEDULE_ERROR);
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean delete(String id) {
        return true;
    }

    private boolean checkScheduleTime(ScheduleEntity oldSchedule, ScheduleEntity newSchedule) {
        if (oldSchedule.getCycle() != newSchedule.getCycle()) {
            return true;
        }
        if (!CollectionUtils.isEqualCollection(oldSchedule.getTime(), newSchedule.getTime())) {
            return true;
        }
        return !CollectionUtils.isEqualCollection(oldSchedule.getWeek(), newSchedule.getWeek());
    }
}
