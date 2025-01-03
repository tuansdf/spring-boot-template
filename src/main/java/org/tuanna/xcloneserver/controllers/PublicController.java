package org.tuanna.xcloneserver.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tuanna.xcloneserver.dtos.CommonResponse;
import org.tuanna.xcloneserver.dtos.TestUser;
import org.tuanna.xcloneserver.modules.excel.TestUserReportTemplate;
import org.tuanna.xcloneserver.utils.*;

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
            log.info("POP");
            List<TestUser> data = new ArrayList<>();
            int TOTAL = 500_000;
            for (int i = 0; i < TOTAL; i++) {
                TestUser user = new TestUser();
                user.setId(UUIDUtils.generateId());
                user.setUsername("username" + i);
                user.setEmail("email" + i);
                user.setName("name" + i);
                user.setAddress("address" + i);
                user.setStreet("street" + i);
                user.setCountry("country" + i);
                user.setCity("city" + i);
                data.add(user);
            }
            log.info("POPEND");
//            log.info("WORKBOOK");
//            ExcelUtils.processTemplate(new TestUserReportTemplate(data), "test-excel-" + DateUtils.getEpochMicro() + ".xlsx");
//            log.info("WORKBOOKEND");
            log.info("CSV");
            String outPath = "test-csv-" + DateUtils.getEpochMicro() + ".csv";
            FileUtils.writeFile(CSVUtils.processTemplateToBytes(new TestUserReportTemplate(data)), outPath);
            log.info("CSVEND");
            return ResponseEntity.ok(new CommonResponse());
        } catch (Exception e) {
            log.error("loi nay", e);
            return ExceptionUtils.toResponseEntity(e);
        }
    }

}
