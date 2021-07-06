package com.sms.satp.common.field;

public enum ApiTestCaseJobFiled implements Filed {

    API_TEST_CASE_ID("apiTestCase.jobApiTestCase.id");

    ApiTestCaseJobFiled(String filed) {
        this.filed = filed;
    }

    private final String filed;

    @Override
    public String getFiled() {
        return filed;
    }
}