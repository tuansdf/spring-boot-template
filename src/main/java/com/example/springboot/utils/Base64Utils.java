package com.example.springboot.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Base64;

@Slf4j
public class Base64Utils {

    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Base64.Decoder decoder = Base64.getDecoder();
    private static final Base64.Encoder urlEncoder = Base64.getUrlEncoder();
    private static final Base64.Decoder urlDecoder = Base64.getUrlDecoder();

    public static String encode(String input) {
        try {
            return encoder.encodeToString(input.getBytes());
        } catch (Exception e) {
            log.error("base64 encode", e);
            return "";
        }
    }

    public static String encodeUrl(String input) {
        try {
            return urlEncoder.encodeToString(input.getBytes());
        } catch (Exception e) {
            log.error("base64 encode url", e);
            return "";
        }
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

    public static String decodeUrl(String input) {
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
