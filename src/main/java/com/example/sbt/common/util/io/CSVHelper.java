package com.example.sbt.common.util.io;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class CSVHelper {

    public static class Export {
        public static void processTemplate(Writer writer, List<String> header, Consumer<CSVPrinter> bodyFn) {
            try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setHeader(header.toArray(new String[0])).build())) {
                bodyFn.accept(csvPrinter);
            } catch (Exception e) {
                log.error("processTemplate ", e);
            }
        }

        public static byte[] processTemplateToBytes(List<String> header, Consumer<CSVPrinter> bodyFn) {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                 OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {
                processTemplate(writer, header, bodyFn);
                return outputStream.toByteArray();
            } catch (Exception e) {
                log.error("processTemplateToBytes ", e);
                return null;
            }
        }

        public static void processTemplateWriteFile(String outputPath, List<String> header, Consumer<CSVPrinter> bodyFn) {
            try (FileWriter writer = new FileWriter(outputPath, StandardCharsets.UTF_8);
                 BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
                processTemplate(bufferedWriter, header, bodyFn);
            } catch (Exception e) {
                log.error("processTemplateWriteFile ", e);
            }
        }
    }

    public static class Import {
        public static void processTemplate(Reader reader, Consumer<CSVParser> bodyFn) {
            try (CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().build())) {
                bodyFn.accept(csvParser);
            } catch (Exception e) {
                log.error("processTemplate ", e);
            }
        }

        public static void processTemplate(String filePath, Consumer<CSVParser> bodyFn) {
            try (FileReader reader = new FileReader(Paths.get(filePath).toFile())) {
                processTemplate(reader, bodyFn);
            } catch (Exception e) {
                log.error("processTemplate ", e);
            }
        }

        public static void processTemplate(byte[] bytes, Consumer<CSVParser> bodyFn) {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                processTemplate(reader, bodyFn);
            } catch (Exception e) {
                log.error("processTemplate ", e);
            }
        }

        public static void processTemplate(MultipartFile file, Consumer<CSVParser> bodyFn) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
                processTemplate(reader, bodyFn);
            } catch (Exception e) {
                log.error("processTemplate ", e);
            }
        }
    }

}
