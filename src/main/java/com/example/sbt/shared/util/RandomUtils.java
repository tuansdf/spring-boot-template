package com.example.sbt.shared.util;

import com.fasterxml.uuid.Generators;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

@Slf4j
public class RandomUtils {
    private static final String DEFAULT_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final char[] DEFAULT_ALPHABET_CHARS = DEFAULT_ALPHABET.toCharArray();

    private static final String OTP_ALPHABET = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final char[] OTP_ALPHABET_CHARS = OTP_ALPHABET.toCharArray();

    private static final int MIN_LENGTH = 1;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final RandomUtils SECURE_UTILS = new RandomUtils(() -> SECURE_RANDOM);
    private static final RandomUtils INSECURE_UTILS = new RandomUtils(ThreadLocalRandom::current);

    private final Supplier<Random> randomSupplier;

    public RandomUtils(Supplier<Random> randomSupplier) {
        this.randomSupplier = randomSupplier;
    }

    public static RandomUtils insecure() {
        return INSECURE_UTILS;
    }

    public static RandomUtils secure() {
        return SECURE_UTILS;
    }

    private static String generateHexString(Random random, int length) {
        if (random == null) {
            random = SECURE_RANDOM;
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
            random = SECURE_RANDOM;
        }
        if (alphabet == null) {
            alphabet = DEFAULT_ALPHABET_CHARS;
        }
        if (length < MIN_LENGTH) {
            length = MIN_LENGTH;
        }
        return RandomStringUtils.random(length, 0, alphabet.length, true, true, alphabet, random);
    }

    public UUID generateUUID() {
        return Generators.randomBasedGenerator(randomSupplier.get()).generate();
    }

    public UUID generateTimeBasedUUID() {
        return Generators.timeBasedEpochRandomGenerator(randomSupplier.get()).generate();
    }

    public String generateHexString(int length) {
        return RandomUtils.generateHexString(randomSupplier.get(), length);
    }

    public String generateOTP(int length) {
        return RandomUtils.generateString(randomSupplier.get(), OTP_ALPHABET_CHARS, length);
    }

    public String generateString(int length) {
        return RandomUtils.generateString(randomSupplier.get(), DEFAULT_ALPHABET_CHARS, length);
    }
}
