package com.example.sbt.common.util.io;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class CSVHelper {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

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
                 OutputStreamWriter writer = new OutputStreamWriter(outputStream, DEFAULT_CHARSET)) {
                processTemplate(writer, header, bodyFn);
                return outputStream.toByteArray();
            } catch (Exception e) {
                log.error("processTemplateToBytes ", e);
                return null;
            }
        }

        public static void processTemplateWriteFile(String filePath, List<String> header, Consumer<CSVPrinter> bodyFn) {
            try (Writer writer = Files.newBufferedWriter(Paths.get(filePath), DEFAULT_CHARSET)) {
                processTemplate(writer, header, bodyFn);
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
            try (Reader reader = Files.newBufferedReader(Paths.get(filePath), DEFAULT_CHARSET)) {
                processTemplate(reader, bodyFn);
            } catch (Exception e) {
                log.error("processTemplate ", e);
            }
        }

        public static void processTemplate(byte[] bytes, Consumer<CSVParser> bodyFn) {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                 InputStreamReader inputStreamReader = new InputStreamReader(inputStream, DEFAULT_CHARSET);
                 BufferedReader reader = new BufferedReader(inputStreamReader)) {
                processTemplate(reader, bodyFn);
            } catch (Exception e) {
                log.error("processTemplate ", e);
            }
        }

        public static void processTemplate(MultipartFile file, Consumer<CSVParser> bodyFn) {
            try (InputStream inputStream = file.getInputStream();
                 InputStreamReader inputStreamReader = new InputStreamReader(inputStream, DEFAULT_CHARSET);
                 BufferedReader reader = new BufferedReader(inputStreamReader)) {
                processTemplate(reader, bodyFn);
            } catch (Exception e) {
                log.error("processTemplate ", e);
            }
        }
    }

}
