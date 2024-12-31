package org.tuanna.xcloneserver.utils;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochRandomGenerator;

import java.util.UUID;

public class UUIDUtils {

    private static final TimeBasedEpochRandomGenerator timeBasedEpochRandomGenerator = Generators.timeBasedEpochRandomGenerator();

    public static UUID generate() {
        return UUID.randomUUID();
    }

    public static UUID generateId() {
        return timeBasedEpochRandomGenerator.generate();
    }

}
