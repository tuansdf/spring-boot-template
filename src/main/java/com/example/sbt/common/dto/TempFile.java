package com.example.sbt.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
@AllArgsConstructor
public class TempFile implements AutoCloseable {
    private final Path path;

    @Override
    public void close() throws IOException {
        Files.deleteIfExists(path);
    }
}
