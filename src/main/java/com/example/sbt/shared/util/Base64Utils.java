package com.example.sbt.shared.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public class Base64Utils {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Base64.Decoder decoder = Base64.getDecoder();
    private static final Base64.Encoder urlEncoder = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder urlDecoder = Base64.getUrlDecoder();

    public static String encode(byte[] bytes) {
        try {
            if (bytes == null) return null;
            return encoder.encodeToString(bytes);
        } catch (Exception e) {
            log.error("base64 encode {}", e.toString());
            return null;
        }
    }

    public static String encode(String input) {
        try {
            if (input == null) return null;
            return encode(input.getBytes(DEFAULT_CHARSET));
        } catch (Exception e) {
            log.error("base64 encode {}", e.toString());
            return null;
        }
    }

    public static String urlEncode(byte[] bytes) {
        try {
            if (bytes == null) return null;
            return urlEncoder.encodeToString(bytes);
        } catch (Exception e) {
            log.error("base64url encode {}", e.toString());
            return null;
        }
    }

    public static String urlEncode(String input) {
        try {
            if (input == null) return null;
            return urlEncode(input.getBytes(DEFAULT_CHARSET));
        } catch (Exception e) {
            log.error("base64url encode {}", e.toString());
            return null;
        }
    }

    public static byte[] decode(String input) {
        try {
            if (StringUtils.isBlank(input)) return null;
            return decoder.decode(input.getBytes());
        } catch (Exception e) {
            log.error("base64 decode {}", e.toString());
            return null;
        }
    }

    public static String decodeToString(String input) {
        try {
            byte[] result = decode(input);
            if (result == null) return null;
            return new String(result, DEFAULT_CHARSET);
        } catch (Exception e) {
            log.error("base64 decode to string {}", e.toString());
            return null;
        }
    }

    public static byte[] urlDecode(String input) {
        try {
            if (StringUtils.isBlank(input)) return null;
            return urlDecoder.decode(input.getBytes());
        } catch (Exception e) {
            log.error("base64url decode {}", e.toString());
            return null;
        }
    }

    public static String urlDecodeToString(String input) {
        try {
            byte[] result = urlDecode(input);
            if (result == null) return null;
            return new String(result, DEFAULT_CHARSET);
        } catch (Exception e) {
            log.error("base64url decode to string {}", e.toString());
            return null;
        }
    }
}
