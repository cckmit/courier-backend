package com.sms.satp.service;

import com.sms.satp.entity.function.FunctionMessage;
import com.sms.satp.websocket.Payload;

public interface MessageService {

    void projectMessage(String projectId, Payload<?> payload);

    void userMessage(String userId, Payload<?> payload);

    void enginePullFunctionMessage(FunctionMessage functionMessage);
}
