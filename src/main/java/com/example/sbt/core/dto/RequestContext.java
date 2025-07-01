package com.example.sbt.core.dto;

import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.core.constant.MDCKey;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

public class RequestContext {

    private static final ThreadLocal<RequestContextData> context = ThreadLocal.withInitial(() -> null);

    public static RequestContextData get() {
        RequestContextData result = context.get();
        if (result == null) {
            result = new RequestContextData();
            context.set(result);
        }
        return result;
    }

    public static void set(RequestContextData input) {
        context.set(input);
        syncMDC();
    }

    public static void clear() {
        context.remove();
        MDC.clear();
    }

    public static void syncMDC() {
        RequestContextData context = get();
        String requestId = ConversionUtils.toString(context.getRequestId());
        if (StringUtils.isNotEmpty(requestId)) {
            MDC.put(MDCKey.REQUEST_ID, requestId);
        }
    }

}
