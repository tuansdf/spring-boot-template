package org.tuanna.xcloneserver.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.ArrayUtils;
import org.tuanna.xcloneserver.modules.excel.ExportTemplate;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

@Slf4j
public class CSVUtils {

    public static <T> void processTemplate(ExportTemplate<T> template, Writer writer) {
        try {
            if (template == null || writer == null || ArrayUtils.isEmpty(template.getHeader()) || CollectionUtils.isEmpty(template.getBody()))
                return;

            String[] header = template.getHeader();
            List<T> body = template.getBody();
            Function<T, Object[]> rowExtractor = template.getRowExtractor();
            try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setHeader(header).build())) {
                for (T data : body) {
                    csvPrinter.printRecord(rowExtractor.apply(data));
                }
            }
        } catch (Exception e) {
            log.error("processTemplate", e);
        }
    }

    public static <T> byte[] processTemplateToBytes(ExportTemplate<T> exportTemplate) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
             OutputStreamWriter writer = new OutputStreamWriter(bufferedOutputStream)) {
            processTemplate(exportTemplate, writer);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("processTemplateToBytes", e);
            return new byte[0];
        }
    }

    public static <T> void processTemplateToFile(ExportTemplate<T> exportTemplate, String outputPath) {
        try (FileWriter writer = new FileWriter(outputPath, StandardCharsets.UTF_8);
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            processTemplate(exportTemplate, bufferedWriter);
        } catch (IOException e) {
            log.error("processTemplateToFile", e);
        }
    }

}
