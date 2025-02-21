package com.example.demo.utils;

import com.example.demo.configs.RequestContextHolder;
import com.example.demo.dtos.RequestContext;
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
