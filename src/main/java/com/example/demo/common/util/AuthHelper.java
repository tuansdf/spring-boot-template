package com.example.demo.common.util;

import com.example.demo.common.dto.RequestContext;
import com.example.demo.config.RequestContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Slf4j
public class AuthHelper {

    public static boolean hasAnyPermission(List<String> permissions) {
        RequestContext context = RequestContextHolder.get();
        if (CollectionUtils.isEmpty(context.getPermissions())) {
            return false;
        }
        return context.getPermissions().stream().anyMatch(permissions::contains);
    }

}
