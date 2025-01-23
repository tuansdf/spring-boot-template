package com.example.springboot.utils;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.RandomBasedGenerator;
import com.fasterxml.uuid.impl.TimeBasedEpochRandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

@Slf4j
public class RandomUtils {

    private static final TimeBasedEpochRandomGenerator timeBasedEpochRandomGenerator = Generators.timeBasedEpochRandomGenerator();
    private static final RandomBasedGenerator randomBasedGenerator = Generators.randomBasedGenerator();
    private static final Random random = new Random();
    private static final SecureRandom secureRandom = new SecureRandom();

    public static UUID generateUUID() {
        return randomBasedGenerator.generate();
    }

    public static UUID generateTimeBasedUUID() {
        return timeBasedEpochRandomGenerator.generate();
    }

    private static String executeGenerateString(Random random, int length) {
        int byteLength = ConversionUtils.safeToInt(Math.ceil(length * 0.75)); // increase size because of base64
        byte[] randomBytes = new byte[byteLength];
        random.nextBytes(randomBytes);
        String result = Base64Helper.urlEncode(randomBytes);
        if (result.length() > length) {
            result = result.substring(0, length);
        }
        return result;
    }

    public static String generateString(int length) {
        return executeGenerateString(secureRandom, length);
    }

    public static String generateInsecuredString(int length) {
        return executeGenerateString(random, length);
    }

    public static String generateOTP(int length) {
        int rand = secureRandom.nextInt(ConversionUtils.safeToInt(Math.pow(10, length)));
        return StringUtils.leftPad(ConversionUtils.toString(rand), length, "0");
    }

}
