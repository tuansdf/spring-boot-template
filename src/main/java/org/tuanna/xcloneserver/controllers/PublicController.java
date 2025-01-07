package org.tuanna.xcloneserver.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tuanna.xcloneserver.mappers.CommonMapper;
import org.tuanna.xcloneserver.modules.report.UserExportTemplate;
import org.tuanna.xcloneserver.modules.report.UserImportTemplate;
import org.tuanna.xcloneserver.modules.user.dtos.UserDTO;
import org.tuanna.xcloneserver.utils.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/public")
public class PublicController {

    private final CommonMapper commonMapper;

    @GetMapping("/health")
    public String check() {
        return "OK";
    }

    private List<UserDTO> createData(int total) {
        List<UserDTO> data = new ArrayList<>();
        OffsetDateTime now = OffsetDateTime.now();

        for (int i = 0; i < total; i++) {
            UserDTO user = new UserDTO();
            user.setId(UUIDUtils.generate());
            user.setUsername(ConversionUtils.safeToString(UUIDUtils.generate()));
            user.setEmail(ConversionUtils.safeToString(UUIDUtils.generate()));
            user.setName(ConversionUtils.safeToString(UUIDUtils.generate()));
            user.setStatus(ConversionUtils.safeToString(UUIDUtils.generate()));
            user.setCreatedBy(UUIDUtils.generate());
            user.setUpdatedBy(UUIDUtils.generate());
            user.setCreatedAt(now.plusSeconds(i));
            user.setUpdatedAt(now.plusMinutes(i));
            data.add(user);
        }
        return data;
    }

    @GetMapping("/export-excel")
    public String exportExcel(@RequestParam(required = false, defaultValue = "1000") Integer total) {
        List<UserDTO> data = createData(total);
        String exportPath = ".temp/excel-" + DateUtils.getEpochMicro() + ".xlsx";
        ExcelUtils.Export.processTemplateWriteFile(new UserExportTemplate(data), exportPath);
        return "OK";
    }

    @GetMapping("/export-excel-batch")
    public String exportExcelBatch(@RequestParam(required = false, defaultValue = "1000") Integer total) {
        int BATCH = 1000;
        List<UserDTO> data = createData(total);
        Workbook workbook = new SXSSFWorkbook();
        UserExportTemplate template = new UserExportTemplate();
        for (int i = 0; i < total; i += BATCH) {
            template.setBody(data.subList(i, Math.min(total, i + BATCH)));
            ExcelUtils.Export.processTemplate(workbook, template);
        }
        String exportPath = ".temp/excel-" + DateUtils.getEpochMicro() + ".xlsx";
        ExcelUtils.writeFile(workbook, exportPath);
        return "OK";
    }

    @GetMapping("/export-csv")
    public String exportCsv(@RequestParam(required = false, defaultValue = "1000") Integer total) {
        List<UserDTO> data = createData(total);
        String exportPath = ".temp/csv-" + DateUtils.getEpochMicro() + ".csv";
        CSVUtils.Export.processTemplateWriteFile(new UserExportTemplate(data), exportPath);
        return "OK";
    }

    @GetMapping("/import-excel")
    public String importExcel(@RequestParam String inputPath) {
        var items = ExcelUtils.Import.processTemplate(new UserImportTemplate(), inputPath);
        log.info("items {}", items.subList(0, Math.min(items.size(), 100)));
        return "OK";
    }

    @GetMapping("/import-csv")
    public String importCsv(@RequestParam String inputPath) {
        var items = CSVUtils.Import.processTemplate(new UserImportTemplate(), inputPath);
        log.info("items {}", items.subList(0, Math.min(items.size(), 100)));
        return "OK";
    }

    @GetMapping("/test-mapper")
    public String mapper() {
        List<UserDTO> data = createData(10);
        log.info("DTOs: {}", data);
        log.info("Entities: {}", data.stream().map(commonMapper::toEntity).toList());
        return "OK";
    }

}
