package com.sms.courier.common.function;

import io.vavr.Function1;

@FunctionalInterface
public interface Function1Handler<T1, R> {

    R handler(Function1<T1, R> confirmed);
}
