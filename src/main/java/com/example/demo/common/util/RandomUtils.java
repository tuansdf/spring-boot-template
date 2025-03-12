package com.example.demo.common.util;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.RandomBasedGenerator;
import com.fasterxml.uuid.impl.TimeBasedEpochRandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

@Slf4j
public class RandomUtils {

    private static final Random insecureRandom = new Random();
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final TimeBasedEpochRandomGenerator secureTimeBasedUUIDGenerator = Generators.timeBasedEpochRandomGenerator(secureRandom);
    private static final TimeBasedEpochRandomGenerator insecureTimeBasedUUIDGenerator = Generators.timeBasedEpochRandomGenerator(insecureRandom);
    private static final RandomBasedGenerator secureUUIDGenerator = Generators.randomBasedGenerator(secureRandom);
    private static final RandomBasedGenerator insecureUUIDGenerator = Generators.randomBasedGenerator(insecureRandom);

    private static String generateHexString(Random random, int length) {
        if (random == null) {
            random = secureRandom;
        }
        byte[] randomBytes = new byte[length];
        random.nextBytes(randomBytes);
        return Hex.encodeHexString(randomBytes);
    }

    private static String generateOTP(Random random, int length) {
        if (random == null) {
            random = secureRandom;
        }
        int rand = random.nextInt(ConversionUtils.safeToInt(Math.pow(10, length)));
        return StringUtils.leftPad(ConversionUtils.safeToString(rand), length, "0");
    }

    public static class Secure {
        public static UUID generateUUID() {
            return secureUUIDGenerator.generate();
        }

        public static UUID generateTimeBasedUUID() {
            return secureTimeBasedUUIDGenerator.generate();
        }

        public static String generateOTP(int length) {
            return RandomUtils.generateOTP(secureRandom, length);
        }

        public static String generateHexString(int length) {
            return RandomUtils.generateHexString(secureRandom, length);
        }
    }

    public static class Insecure {
        public static UUID generateUUID() {
            return insecureUUIDGenerator.generate();
        }

        public static UUID generateTimeBasedUUID() {
            return insecureTimeBasedUUIDGenerator.generate();
        }

        public static String generateOTP(int length) {
            return RandomUtils.generateOTP(insecureRandom, length);
        }

        public static String generateHexString(int length) {
            return RandomUtils.generateHexString(insecureRandom, length);
        }
    }

}
