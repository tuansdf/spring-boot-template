package org.tuanna.xcloneserver.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tuanna.xcloneserver.dtos.TestUser;
import org.tuanna.xcloneserver.entities.User;
import org.tuanna.xcloneserver.modules.excel.ExcelCellConfig;
import org.tuanna.xcloneserver.modules.excel.ExcelUtils;
import org.tuanna.xcloneserver.utils.UUIDUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public String excel() throws IOException {
        List<ExcelCellConfig> cellConfigs = new ArrayList<>();
        cellConfigs.add(ExcelCellConfig.builder().row(0).col(2).colMergedTo(5).rowMergedTo(1).value("date").build());
        cellConfigs.add(ExcelCellConfig.builder().row(0).col(1).value("de len nay").build());
        Map<String, Integer> mapper = new HashMap<>();
        mapper.put("getId", 1);
        mapper.put("getUser", 2);
        mapper.put("getEmail", 3);
        mapper.put("getName", 4);
        List<TestUser> data = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            TestUser user = new TestUser();
            user.setId(i);
            user.setUser("username" + i);
            user.setEmail("email" + i);
            user.setName("name" + i);
            data.add(user);
        }
        cellConfigs.add(ExcelCellConfig.builder().row(2).col(3).value(data).mapper(mapper).build());
        ExcelUtils.exportReport(cellConfigs, "test-output-excel.xlsx");
        return "OK";
    }

}
