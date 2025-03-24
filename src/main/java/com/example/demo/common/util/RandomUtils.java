package com.example.demo.common.util;

import com.fasterxml.uuid.Generators;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class RandomUtils {

    private static final String DEFAULT_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final char[] DEFAULT_ALPHABET_CHARS = DEFAULT_ALPHABET.toCharArray();

    private static final String OTP_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final char[] OTP_ALPHABET_CHARS = OTP_ALPHABET.toCharArray();

    private static final int MIN_LENGTH = 1;

    private static final SecureRandom secureRandom = new SecureRandom();

    private static String generateHexString(Random random, int length) {
        if (random == null) {
            random = secureRandom;
        }
        if (length < MIN_LENGTH) {
            length = MIN_LENGTH;
        }
        byte[] randomBytes = new byte[length];
        random.nextBytes(randomBytes);
        return Hex.encodeHexString(randomBytes);
    }

    private static String generateString(Random random, char[] alphabet, int length) {
        if (random == null) {
            random = secureRandom;
        }
        if (alphabet == null) {
            alphabet = DEFAULT_ALPHABET_CHARS;
        }
        if (length < MIN_LENGTH) {
            length = MIN_LENGTH;
        }
        return RandomStringUtils.random(length, 0, alphabet.length, true, true, alphabet, random);
    }

    public static class Secure {
        public static UUID generateUUID() {
            return Generators.randomBasedGenerator(secureRandom).generate();
        }

        public static UUID generateTimeBasedUUID() {
            return Generators.timeBasedEpochRandomGenerator(secureRandom).generate();
        }

        public static String generateHexString(int length) {
            return RandomUtils.generateHexString(secureRandom, length);
        }

        public static String generateOTP(int length) {
            return RandomUtils.generateString(secureRandom, OTP_ALPHABET_CHARS, length);
        }

        public static String generateString(int length) {
            return RandomUtils.generateString(secureRandom, DEFAULT_ALPHABET_CHARS, length);
        }
    }

    public static class Insecure {
        public static UUID generateUUID() {
            return Generators.randomBasedGenerator(ThreadLocalRandom.current()).generate();
        }

        public static UUID generateTimeBasedUUID() {
            return Generators.timeBasedEpochRandomGenerator(ThreadLocalRandom.current()).generate();
        }

        public static String generateHexString(int length) {
            return RandomUtils.generateHexString(ThreadLocalRandom.current(), length);
        }

        public static String generateOTP(int length) {
            return RandomUtils.generateString(ThreadLocalRandom.current(), OTP_ALPHABET_CHARS, length);
        }

        public static String generateString(int length) {
            return RandomUtils.generateString(ThreadLocalRandom.current(), DEFAULT_ALPHABET_CHARS, length);
        }
    }

}
