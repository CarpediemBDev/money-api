package com.kpay.api.handler;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class ApiResponseMessage {
    @JsonUnwrapped
    private String code;
    @JsonUnwrapped
    private Object result;

    public ApiResponseMessage(String code, Object result) {
        this.code = code;
        this.result = result;
    }
}
