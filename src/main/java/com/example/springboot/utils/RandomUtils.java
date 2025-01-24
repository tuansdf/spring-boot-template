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

    private static final Random insecureRandom = new Random();
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final TimeBasedEpochRandomGenerator timeBasedEpochRandomGenerator = Generators.timeBasedEpochRandomGenerator();
    private static final TimeBasedEpochRandomGenerator insecureTimeBasedEpochRandomGenerator = Generators.timeBasedEpochRandomGenerator(insecureRandom);
    private static final RandomBasedGenerator randomBasedGenerator = Generators.randomBasedGenerator();
    private static final RandomBasedGenerator insecureRandomBasedGenerator = Generators.randomBasedGenerator(insecureRandom);

    public static UUID generateUUID() {
        return randomBasedGenerator.generate();
    }

    public static UUID generateTimeBasedUUID() {
        return timeBasedEpochRandomGenerator.generate();
    }

    private static String generateString(Random random, int length) {
        if (random == null) {
            random = secureRandom;
        }
        int byteLength = ConversionUtils.safeToInt(Math.ceil(length * 0.75)); // increase size because of base64
        byte[] randomBytes = new byte[byteLength];
        random.nextBytes(randomBytes);
        String result = Base64Helper.urlEncode(randomBytes);
        if (result.length() > length) {
            result = result.substring(0, length);
        }
        return result;
    }

    private static String generateOTP(Random random, int length) {
        if (random == null) {
            random = secureRandom;
        }
        int rand = random.nextInt(ConversionUtils.safeToInt(Math.pow(10, length)));
        return StringUtils.leftPad(ConversionUtils.toString(rand), length, "0");
    }

    public static class Secure {
        public static String generateOTP(int length) {
            return RandomUtils.generateOTP(secureRandom, length);
        }

        public static String generateString(int length) {
            return RandomUtils.generateString(secureRandom, length);
        }
    }

    public static class Insecure {
        public static UUID generateUUID() {
            return insecureRandomBasedGenerator.generate();
        }

        public static UUID generateTimeBasedUUID() {
            return insecureTimeBasedEpochRandomGenerator.generate();
        }

        public static String generateOTP(int length) {
            return RandomUtils.generateOTP(insecureRandom, length);
        }

        public static String generateString(int length) {
            return RandomUtils.generateString(insecureRandom, length);
        }
    }

}
