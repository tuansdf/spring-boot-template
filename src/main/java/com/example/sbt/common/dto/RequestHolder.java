package com.example.sbt.common.dto;

import com.example.sbt.common.constant.MDCKey;
import com.example.sbt.common.util.ConversionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

public class RequestHolder {

    private static final ThreadLocal<RequestContext> context = new ThreadLocal<>();

    public static RequestContext getContext() {
        RequestContext result = context.get();
        if (result == null) {
            result = new RequestContext();
            context.set(result);
        }
        return result;
    }

    public static void setContext(RequestContext input) {
        context.set(input);
        syncMDC();
    }

    public static void clear() {
        context.remove();
        MDC.clear();
    }

    public static void syncMDC() {
        RequestContext context = getContext();
        String requestId = ConversionUtils.toString(context.getRequestId());
        if (StringUtils.isNotEmpty(requestId)) {
            MDC.put(MDCKey.REQUEST_ID, requestId);
        }
    }

}
