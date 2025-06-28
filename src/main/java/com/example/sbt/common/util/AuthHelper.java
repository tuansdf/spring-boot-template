package com.example.sbt.common.util;

import com.example.sbt.common.dto.RequestContextData;
import com.example.sbt.common.dto.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;

@Slf4j
public class AuthHelper {

    public static boolean hasAnyPermission(String... permissions) {
        RequestContextData context = RequestContext.get();
        if (CollectionUtils.isEmpty(context.getPermissions())) {
            return false;
        }
        return Arrays.stream(permissions).anyMatch(x -> context.getPermissions().contains(x));
    }

}
