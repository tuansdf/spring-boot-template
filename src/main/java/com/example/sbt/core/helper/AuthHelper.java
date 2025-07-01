package com.example.sbt.core.helper;

import com.example.sbt.core.dto.RequestContext;
import com.example.sbt.core.dto.RequestContextData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
public class AuthHelper {

    public boolean hasAnyPermission(String... permissions) {
        RequestContextData context = RequestContext.get();
        if (CollectionUtils.isEmpty(context.getPermissions())) {
            return false;
        }
        return Arrays.stream(permissions).anyMatch(x -> context.getPermissions().contains(x));
    }

}
