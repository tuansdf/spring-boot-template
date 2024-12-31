package org.tuanna.xcloneserver.utils;

import java.util.Base64;

public class Base64Utils {

    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Base64.Decoder decoder = Base64.getDecoder();
    private static final Base64.Encoder urlEncoder = Base64.getUrlEncoder();
    private static final Base64.Decoder urlDecoder = Base64.getUrlDecoder();

    public static String encode(String input) {
        return encoder.encodeToString(input.getBytes());
    }

    public static String encodeUrl(String input) {
        return urlEncoder.encodeToString(input.getBytes());
    }

    public static String decode(String input) {
        byte[] result = decoder.decode(input.getBytes());
        if (result == null) return "";
        return new String(result);
    }

    public static String decodeUrl(String input) {
        byte[] result = urlDecoder.decode(input.getBytes());
        if (result == null) return "";
        return new String(result);
    }

}
