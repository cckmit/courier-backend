package com.sms.satp.common.field;

public enum ApiTestCaseJobField implements Field {

    API_TEST_CASE_ID("apiTestCase.jobApiTestCase.id"),

    JOB_API_ID("apiTestCase.jobApiTestCase.apiId");

    ApiTestCaseJobField(String name) {
        this.name = name;
    }

    private final String name;

    @Override
    public String getName() {
        return name;
    }
}