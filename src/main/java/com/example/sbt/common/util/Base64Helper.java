package com.example.sbt.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public class Base64Helper {

    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Base64.Decoder decoder = Base64.getDecoder();
    private static final Base64.Encoder urlEncoder = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder urlDecoder = Base64.getUrlDecoder();

    public static String encode(byte[] bytes) {
        try {
            return encoder.encodeToString(bytes);
        } catch (Exception e) {
            log.error("base64 encode", e);
            return "";
        }
    }

    public static String encode(String input) {
        if (StringUtils.isBlank(input)) return "";
        return encode(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String urlEncode(byte[] bytes) {
        try {
            return urlEncoder.encodeToString(bytes);
        } catch (Exception e) {
            log.error("base64 encode url", e);
            return "";
        }
    }

    public static String urlEncode(String input) {
        if (StringUtils.isBlank(input)) return "";
        return urlEncode(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String decode(String input) {
        try {
            byte[] result = decoder.decode(input.getBytes());
            if (result == null) return "";
            return new String(result);
        } catch (Exception e) {
            log.error("base64 decode", e);
            return "";
        }
    }

    public static String urlDecode(String input) {
        try {
            byte[] result = urlDecoder.decode(input.getBytes());
            if (result == null) return "";
            return new String(result);
        } catch (Exception e) {
            log.error("base64 decode url", e);
            return "";
        }
    }

}
