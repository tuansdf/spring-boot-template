package com.example.sbt.core.dto;

import com.example.sbt.core.constant.LoggerKey;
import com.example.sbt.shared.util.ConversionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

public class RequestContextHolder {
    private static final ThreadLocal<RequestContext> context = ThreadLocal.withInitial(() -> null);

    public static RequestContext get() {
        RequestContext result = context.get();
        if (result == null) {
            result = new RequestContext();
            context.set(result);
        }
        return result;
    }

    public static void set(RequestContext input) {
        context.set(input);
        syncWithLogger();
    }

    public static void clear() {
        context.remove();
        MDC.clear();
    }

    public static void syncWithLogger() {
        RequestContext context = get();
        String requestId = ConversionUtils.toString(context.getRequestId());
        String username = ConversionUtils.toString(context.getUsername());
        if (StringUtils.isNotEmpty(requestId)) {
            MDC.put(LoggerKey.REQUEST_ID, requestId);
        }
        if (StringUtils.isNotEmpty(username)) {
            MDC.put(LoggerKey.USERNAME, username);
        }
    }
}
