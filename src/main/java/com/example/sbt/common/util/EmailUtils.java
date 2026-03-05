package com.example.sbt.common.util;

import org.apache.commons.lang3.StringUtils;

public class EmailUtils {
    private EmailUtils() {
    }

    public static String extractDomain(String email) {
        if (StringUtils.isBlank(email)) return null;
        int atIndex = email.lastIndexOf('@');
        if (atIndex < 0 || atIndex >= email.length() - 1) return null;
        return email.substring(atIndex + 1);
    }
}
