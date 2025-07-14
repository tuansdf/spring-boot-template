package com.example.sbt.core.helper;

import com.example.sbt.shared.util.ConversionUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

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

    public String createFileKey(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try (InputStream inputStream = file.getInputStream()) {
            byte[] hash = DigestUtils.sha1(inputStream);
            return Base64.encodeBase64URLSafeString(hash);
        } catch (Exception e) {
            return null;
        }
    }
}
