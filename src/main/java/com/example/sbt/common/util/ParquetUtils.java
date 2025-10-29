package com.example.sbt.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetFileWriter;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.io.LocalInputFile;
import org.apache.parquet.io.LocalOutputFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Slf4j
public class ParquetUtils {
    public static <T> void write(Path outputPath, Schema schema, List<T> data, Function<T, GenericRecord> mapper) {
        if (data == null || data.isEmpty()) return;
        try {
            try (ParquetWriter<GenericRecord> writer = AvroParquetWriter.<GenericRecord>builder(new LocalOutputFile(outputPath))
                    .withSchema(schema)
                    .withCompressionCodec(CompressionCodecName.SNAPPY)
                    .withWriteMode(ParquetFileWriter.Mode.OVERWRITE)
                    .build()) {
                for (T item : data) {
                    writer.write(mapper.apply(item));
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public static <T> List<T> read(Path path, Function<GenericRecord, T> mapper) {
        List<T> data = new ArrayList<>();
        try (ParquetReader<GenericRecord> reader = AvroParquetReader.<GenericRecord>builder(new LocalInputFile(path)).build()) {
            GenericRecord record;
            while ((record = reader.read()) != null) {
                data.add(mapper.apply(record));
            }
            return data;
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }
}
