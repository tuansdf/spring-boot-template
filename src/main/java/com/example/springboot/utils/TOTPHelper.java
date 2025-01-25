package com.example.springboot.utils;

import com.atlassian.onetime.core.TOTP;
import com.atlassian.onetime.core.TOTPGenerator;
import com.atlassian.onetime.model.TOTPSecret;
import com.atlassian.onetime.service.DefaultTOTPService;
import com.atlassian.onetime.service.RandomSecretProvider;

public class TOTPHelper {

    private static final TOTPGenerator totpGenerator = new TOTPGenerator();
    private static final RandomSecretProvider randomSecretProvider = new RandomSecretProvider();
    private static final DefaultTOTPService totpService = new DefaultTOTPService();

    public static String generateSecret() {
        return randomSecretProvider.generateSecret().getBase32Encoded();
    }

    public static boolean verify(String input, String secret) {
        try {
            var result = totpService.verify(new TOTP(input), TOTPSecret.Companion.fromBase32EncodedString(secret));
            return ConversionUtils.safeToBool(result.isSuccess());
        } catch (Exception e) {
            return false;
        }
    }

}
