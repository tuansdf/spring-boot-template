package org.tuanna.xcloneserver.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.ArrayUtils;
import org.tuanna.xcloneserver.modules.excel.ReportTemplate;

import java.io.*;
import java.util.List;
import java.util.function.Function;

@Slf4j
public class CSVUtils {

    public static <T> void processTemplate(ReportTemplate<T> reportTemplate, Writer writer) {
        try {
            String[] header = reportTemplate.getHeader();
            List<T> body = reportTemplate.getBody();
            if (ArrayUtils.isEmpty(header) || CollectionUtils.isEmpty(body)) return;

            Function<T, Object[]> rowExtractor = reportTemplate.getRowExtractor();
            try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setHeader(header).build())) {
                for (T data : body) {
                    csvPrinter.printRecord(rowExtractor.apply(data));
                }
            }
        } catch (Exception e) {
            log.error("processTemplate", e);
        }
    }

    public static <T> byte[] processTemplateToBytes(ReportTemplate<T> reportTemplate) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
             OutputStreamWriter writer = new OutputStreamWriter(bufferedOutputStream)) {
            processTemplate(reportTemplate, writer);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("processTemplateToBytes", e);
            return new byte[0];
        }
    }

    public static <T> void processTemplateToFile(ReportTemplate<T> reportTemplate, String outputPath) {
        try (FileWriter writer = new FileWriter(outputPath);
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            processTemplate(reportTemplate, bufferedWriter);
        } catch (IOException e) {
            log.error("processTemplateToFile", e);
        }
    }

}
