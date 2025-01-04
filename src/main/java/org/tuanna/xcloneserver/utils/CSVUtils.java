package org.tuanna.xcloneserver.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.tuanna.xcloneserver.modules.report.ExportTemplate;
import org.tuanna.xcloneserver.modules.report.ImportTemplate;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CSVUtils {

    public static <T> void exportTemplate(ExportTemplate<T> template, Writer writer) {
        try {
            if (template == null || writer == null || CollectionUtils.isEmpty(template.getHeader()) || CollectionUtils.isEmpty(template.getBody()))
                return;

            var header = template.getHeader();
            var body = template.getBody();
            var rowExtractor = template.getRowExtractor();
            try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setHeader(header.toArray(new String[0])).build())) {
                for (T data : body) {
                    csvPrinter.printRecord(rowExtractor.apply(data));
                }
            }
        } catch (Exception e) {
            log.error("exportTemplate", e);
        }
    }

    public static <T> byte[] exportTemplateToBytes(ExportTemplate<T> exportTemplate) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
             OutputStreamWriter writer = new OutputStreamWriter(bufferedOutputStream)) {
            exportTemplate(exportTemplate, writer);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("exportTemplateToBytes", e);
            return new byte[0];
        }
    }

    public static <T> void exportTemplateToFile(ExportTemplate<T> exportTemplate, String outputPath) {
        try (FileWriter writer = new FileWriter(outputPath, StandardCharsets.UTF_8);
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            exportTemplate(exportTemplate, bufferedWriter);
        } catch (Exception e) {
            log.error("exportTemplateToFile", e);
        }
    }

    public static <T> List<T> importTemplate(ImportTemplate<T> template, String inputPath) {
        List<T> result = new ArrayList<>();

        if (template == null || CollectionUtils.isEmpty(template.getHeader())) return result;

        var header = template.getHeader();
        var rowExtractor = template.getRowExtractor();

        try (FileReader reader = new FileReader(Paths.get(inputPath).toFile());
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().build())) {

            List<String> csvHeader = csvParser.getHeaderNames();
            if (csvHeader.size() != header.size()) return result;
            for (int i = 0; i < csvHeader.size(); i++) {
                if (!csvHeader.get(i).equals(header.get(i))) return result;
            }

            for (CSVRecord record : csvParser) {
                result.add(rowExtractor.apply(record.stream().toList()));
            }
        } catch (Exception e) {
            log.error("importTemplate", e);
        }

        return result;
    }

}
