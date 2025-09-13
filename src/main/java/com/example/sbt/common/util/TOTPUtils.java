package com.example.sbt.common.util;

import com.atlassian.onetime.core.TOTP;
import com.atlassian.onetime.model.TOTPSecret;
import com.atlassian.onetime.service.DefaultTOTPService;
import com.atlassian.onetime.service.RandomSecretProvider;

public class TOTPUtils {
    public static String generateSecret() {
        return new RandomSecretProvider().generateSecret().getBase32Encoded();
    }

    public static boolean verify(String input, String secret) {
        try {
            var result = new DefaultTOTPService().verify(new TOTP(input), TOTPSecret.Companion.fromBase32EncodedString(secret));
            return ConversionUtils.safeToBoolean(result.isSuccess());
        } catch (Exception e) {
            return false;
        }
    }
}
