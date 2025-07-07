package com.example.sbt.core.helper;

import com.example.sbt.shared.util.ConversionUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class CommonHelper {
    public String createCacheKey(Object input) {
        try {
            if (input == null) {
                return null;
            }
            byte[] hash = DigestUtils.sha1(ConversionUtils.safeToString(input));
            return Base64.encodeBase64URLSafeString(hash);
        } catch (Exception e) {
            return null;
        }
    }
}
