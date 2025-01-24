package com.example.springboot.utils;

import com.example.springboot.modules.report.ExportTemplate;
import com.example.springboot.modules.report.ImportTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CSVHelper {

    public static class Export {
        public static <T> void processTemplate(ExportTemplate<T> template, Writer writer) {
            if (template == null || writer == null || CollectionUtils.isEmpty(template.getHeader()) ||
                    CollectionUtils.isEmpty(template.getBody()) || template.getRowExtractor() == null)
                return;

            var header = template.getHeader();
            var body = template.getBody();
            var rowDataExtractor = template.getRowExtractor();

            try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setHeader(header.toArray(new String[0])).build())) {
                for (T data : body) {
                    csvPrinter.printRecord(rowDataExtractor.apply(data));
                }
            } catch (Exception e) {
                log.error("processTemplate ", e);
            }
        }

        public static <T> byte[] processTemplateToBytes(ExportTemplate<T> exportTemplate) {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
                 OutputStreamWriter writer = new OutputStreamWriter(bufferedOutputStream)) {
                processTemplate(exportTemplate, writer);
                return outputStream.toByteArray();
            } catch (Exception e) {
                log.error("processTemplateToBytes", e);
                return new byte[0];
            }
        }

        public static <T> void processTemplateWriteFile(ExportTemplate<T> exportTemplate, String outputPath) {
            try (FileWriter writer = new FileWriter(outputPath, StandardCharsets.UTF_8);
                 BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
                processTemplate(exportTemplate, bufferedWriter);
            } catch (Exception e) {
                log.error("processTemplateWriteFile", e);
            }
        }
    }

    public static class Import {
        public static <T> List<T> processTemplate(ImportTemplate<T> template, Reader reader) {
            List<T> result = new ArrayList<>();

            if (template == null || CollectionUtils.isEmpty(template.getHeader())) return result;

            var header = template.getHeader();
            var rowExtractor = template.getRowExtractor();

            try (CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().build())) {
                List<String> csvHeader = csvParser.getHeaderNames();
                if (csvHeader.size() != header.size()) return result;
                for (int i = 0; i < csvHeader.size(); i++) {
                    if (!csvHeader.get(i).equals(header.get(i))) return result;
                }

                for (CSVRecord record : csvParser) {
                    result.add(rowExtractor.apply(record.stream().map(x -> (Object) x).toList()));
                }
            } catch (Exception e) {
                log.error("processTemplate ", e);
            }

            return result;
        }

        public static <T> List<T> processTemplate(ImportTemplate<T> template, String filePath) {
            try (FileReader reader = new FileReader(Paths.get(filePath).toFile())) {
                return processTemplate(template, reader);
            } catch (Exception e) {
                log.error("processTemplate ", e);
                return new ArrayList<>();
            }
        }

        public static <T> List<T> processTemplate(ImportTemplate<T> template, byte[] bytes) {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                return processTemplate(template, reader);
            } catch (Exception e) {
                log.error("processTemplate ", e);
                return new ArrayList<>();
            }
        }

        public static <T> List<T> processTemplate(ImportTemplate<T> template, MultipartFile file) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
                return processTemplate(template, reader);
            } catch (Exception e) {
                log.error("processTemplate ", e);
                return new ArrayList<>();
            }
        }
    }

}
