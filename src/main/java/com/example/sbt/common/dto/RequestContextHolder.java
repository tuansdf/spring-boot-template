package com.example.sbt.common.dto;

import com.example.sbt.common.constant.LoggerKey;
import com.example.sbt.common.util.ConversionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

public class RequestContextHolder {
    private static final ThreadLocal<RequestContext> context = ThreadLocal.withInitial(() -> null);

    public static RequestContext get() {
        RequestContext result = context.get();
        if (result == null) {
            return RequestContext.builder().build();
        }
        return result;
    }

    public static void set(RequestContext input) {
        context.set(input);
        syncWithLogger(input);
    }

    public static void clear() {
        context.remove();
        MDC.clear();
    }

    private static void syncWithLogger(RequestContext context) {
        if (context == null) return;
        String requestId = ConversionUtils.toString(context.getRequestId());
        if (StringUtils.isNotBlank(requestId)) {
            MDC.put(LoggerKey.REQUEST_ID, requestId);
        }
    }
}
