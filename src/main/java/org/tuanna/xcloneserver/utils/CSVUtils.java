package org.tuanna.xcloneserver.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.tuanna.xcloneserver.modules.excel.ReportTemplate;

import java.io.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CSVUtils {

    public static void processTemplate(ReportTemplate reportTemplate, Writer writer) {
        try {
            if (CollectionUtils.isEmpty(reportTemplate.getHeader()) || CollectionUtils.isEmpty(reportTemplate.getBody()) || CollectionUtils.isEmpty(reportTemplate.getMapper()))
                return;

            try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(reportTemplate.getHeader().toArray(new String[0])))) {
                Class<?> dataClass = reportTemplate.getBody().getFirst().getClass();
                List<String> mapperKeys = reportTemplate.getMapper().entrySet().stream()
                        .sorted(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .toList();
                Map<String, Method> methods = new HashMap<>();
                for (String key : mapperKeys) {
                    try {
                        methods.put(key, dataClass.getMethod("get" + StringUtils.capitalize(key)));
                    } catch (Exception ignored) {
                    }
                }
                mapperKeys = mapperKeys.stream().filter(x -> methods.get(x) != null).toList();

                for (int i = 0; i < reportTemplate.getBody().size(); i++) {
                    Object data = reportTemplate.getBody().get(i);
                    Object[] records = mapperKeys.stream().map(x -> {
                        try {
                            return methods.get(x).invoke(data);
                        } catch (Exception ignored) {
                            return "";
                        }
                    }).toArray();
                    csvPrinter.printRecord(records);
                }
            }
        } catch (Exception e) {
            log.error("processTemplate", e);
        }
    }

    public static byte[] processTemplateToBytes(ReportTemplate reportTemplate) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
             OutputStreamWriter writer = new OutputStreamWriter(bufferedOutputStream)) {
            processTemplate(reportTemplate, writer);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("processTemplateToBytes", e);
            return new byte[]{};
        }
    }

}
