package org.tuanna.xcloneserver.utils;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.RandomBasedGenerator;
import com.fasterxml.uuid.impl.TimeBasedEpochRandomGenerator;

import java.util.UUID;

public class UUIDUtils {

    private static final TimeBasedEpochRandomGenerator timeBasedEpochRandomGenerator = Generators.timeBasedEpochRandomGenerator();
    private static final RandomBasedGenerator randomBasedGenerator = Generators.randomBasedGenerator();

    public static UUID generate() {
        return randomBasedGenerator.generate();
    }

    public static UUID generateId() {
        return timeBasedEpochRandomGenerator.generate();
    }

}
