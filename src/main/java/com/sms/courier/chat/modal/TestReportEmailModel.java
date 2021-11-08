package com.sms.courier.chat.modal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestReportEmailModel {

    private String name;

    private String projectId;

    private Integer totalTimeCost;

    private Integer paramsTotalTimeCost;

    private Integer delayTimeTotalTimeCost;

    private Integer success;

    private Integer fail;
}