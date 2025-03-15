package com.example.demo.common.util;

import com.example.demo.common.dto.RequestContext;
import com.example.demo.common.dto.RequestContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;

@Slf4j
public class AuthHelper {

    public static boolean hasAnyPermission(String... permissions) {
        RequestContext context = RequestContextHolder.get();
        if (CollectionUtils.isEmpty(context.getPermissions())) {
            return false;
        }
        return Arrays.stream(permissions).anyMatch(x -> context.getPermissions().contains(x));
    }

}
