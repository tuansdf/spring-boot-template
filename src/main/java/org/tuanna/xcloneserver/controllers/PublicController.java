package org.tuanna.xcloneserver.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tuanna.xcloneserver.dtos.TestUser;
import org.tuanna.xcloneserver.modules.report.TestUserExportTemplate;
import org.tuanna.xcloneserver.modules.report.TestUserImportTemplate;
import org.tuanna.xcloneserver.utils.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/public")
public class PublicController {

    @GetMapping("/health")
    public String check() {
        return "OK";
    }

    private List<TestUser> createData(int total) {
        List<TestUser> data = new ArrayList<>();
        ZonedDateTime now = ZonedDateTime.now();

        for (int i = 0; i < total; i++) {
            TestUser user = new TestUser();
            user.setId(UUIDUtils.generate());
            user.setUsername(CommonUtils.safeToString(UUIDUtils.generate()));
            user.setEmail(CommonUtils.safeToString(UUIDUtils.generate()));
            user.setName(CommonUtils.safeToString(UUIDUtils.generate()));
            user.setAddress(CommonUtils.safeToString(UUIDUtils.generate()));
            user.setStreet(CommonUtils.safeToString(UUIDUtils.generate()));
            user.setCountry(CommonUtils.safeToString(UUIDUtils.generate()));
            user.setCity(CommonUtils.safeToString(UUIDUtils.generate()));
            user.setCreatedAt(now.plusSeconds(i));
            user.setUpdatedAt(now.plusMinutes(i));
            data.add(user);
        }
        return data;
    }

    @GetMapping("/export-excel")
    public String exportExcel(@RequestParam(required = false, defaultValue = "1000") Integer total) {
        List<TestUser> data = createData(total);
        String exportPath = ".temp/excel-" + DateUtils.getEpochMicro() + ".xlsx";
        ExcelUtils.exportTemplateToFile(new TestUserExportTemplate(data), exportPath);
        return "OK";
    }

    @GetMapping("/export-excel-batch")
    public String exportExcelBatch(@RequestParam(required = false, defaultValue = "1000") Integer total) {
        int BATCH = 1000;
        List<TestUser> data = createData(total);
        Workbook workbook = new SXSSFWorkbook();
        TestUserExportTemplate template = new TestUserExportTemplate();
        for (int i = 0; i < total; i += BATCH) {
            template.setBody(data.subList(i, Math.min(total, i + BATCH)));
            ExcelUtils.exportTemplate(workbook, template);
        }
        String exportPath = ".temp/excel-" + DateUtils.getEpochMicro() + ".xlsx";
        ExcelUtils.writeFile(workbook, exportPath);
        return "OK";
    }

    @GetMapping("/export-csv")
    public String exportCsv(@RequestParam(required = false, defaultValue = "1000") Integer total) {
        List<TestUser> data = createData(total);
        String exportPath = ".temp/csv-" + DateUtils.getEpochMicro() + ".csv";
        CSVUtils.exportTemplateToFile(new TestUserExportTemplate(data), exportPath);
        return "OK";
    }

    @GetMapping("/import-excel")
    public String importExcel(@RequestParam String inputPath) {
        var items = ExcelUtils.importTemplate(new TestUserImportTemplate(), inputPath);
        log.info("items {}", items.subList(0, Math.min(items.size(), 100)));
        return "OK";
    }

    @GetMapping("/import-csv")
    public String importCsv(@RequestParam String inputPath) {
        var items = CSVUtils.importTemplate(new TestUserImportTemplate(), inputPath);
        log.info("items {}", items.subList(0, Math.min(items.size(), 100)));
        return "OK";
    }

}
