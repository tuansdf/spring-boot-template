package com.example.demo.config;

import com.example.demo.constant.MDCKey;
import com.example.demo.dto.RequestContext;
import com.example.demo.util.ConversionUtils;
import org.slf4j.MDC;

public class RequestContextHolder {

    private static final ThreadLocal<RequestContext> context = new ThreadLocal<>();

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
        syncMDC();
    }

    public static void clear() {
        context.remove();
        MDC.clear();
    }

    public static void syncMDC() {
        RequestContext context = get();
        MDC.put(MDCKey.USER_ID, ConversionUtils.safeToString(context.getUserId()));
        MDC.put(MDCKey.REQUEST_ID, ConversionUtils.safeToString(context.getRequestId()));
    }

}
