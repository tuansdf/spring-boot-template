package org.tuanna.xcloneserver.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tuanna.xcloneserver.dtos.CommonResponse;
import org.tuanna.xcloneserver.dtos.TestUser;
import org.tuanna.xcloneserver.modules.excel.TestUserExportTemplate;
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

    @GetMapping("/excel")
    public ResponseEntity<CommonResponse> excel() {
        try {
            log.info("START");
            List<TestUser> data = new ArrayList<>();
            int TOTAL = 1_000_000;
            ZonedDateTime now = ZonedDateTime.now();
            for (int i = 0; i < TOTAL; i++) {
                TestUser user = new TestUser();
                user.setId(UUIDUtils.generate());
                user.setUsername(String.valueOf(UUIDUtils.generate()));
                user.setEmail(String.valueOf(UUIDUtils.generate()));
                user.setName(String.valueOf(UUIDUtils.generate()));
                user.setAddress(String.valueOf(UUIDUtils.generate()));
                user.setStreet(String.valueOf(UUIDUtils.generate()));
                user.setCountry(String.valueOf(UUIDUtils.generate()));
                user.setCity(String.valueOf(UUIDUtils.generate()));
                user.setCreatedAt(now.plusSeconds(i));
                user.setUpdatedAt(now.plusMinutes(i));
                data.add(user);
            }
            TestUserExportTemplate exportTemplate = new TestUserExportTemplate(data);
            String excelPath = ".temp/test-excel-" + DateUtils.getEpochMicro() + ".xlsx";
            ExcelUtils.processTemplateToFile(exportTemplate, excelPath);

            String csvPath = ".temp/test-csv-" + DateUtils.getEpochMicro() + ".csv";
            String compressedCsvPath = ".temp/test-csv-" + DateUtils.getEpochMicro() + ".csv.gz";
            byte[] csvBytes = CSVUtils.processTemplateToBytes(exportTemplate);
            FileUtils.writeFile(csvBytes, csvPath);
            byte[] compressedCsvBytes = CompressUtils.toGzip(csvBytes);
            FileUtils.writeFile(compressedCsvBytes, compressedCsvPath);
            return ResponseEntity.ok(new CommonResponse(HttpStatus.OK));
        } catch (Exception e) {
            log.error("loi nay", e);
            return ExceptionUtils.toResponseEntity(e);
        }
    }

}
