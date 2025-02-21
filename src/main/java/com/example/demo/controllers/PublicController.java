package com.example.demo.controllers;

import com.example.demo.constants.CommonStatus;
import com.example.demo.dtos.CommonResponse;
import com.example.demo.entities.User;
import com.example.demo.mappers.CommonMapper;
import com.example.demo.modules.configuration.ConfigurationService;
import com.example.demo.modules.configuration.dtos.ConfigurationDTO;
import com.example.demo.modules.jwt.JWTService;
import com.example.demo.modules.jwt.dtos.JWTPayload;
import com.example.demo.modules.report.UserExportTemplate;
import com.example.demo.modules.report.UserImportTemplate;
import com.example.demo.modules.role.RoleService;
import com.example.demo.modules.user.UserRepository;
import com.example.demo.modules.user.UserService;
import com.example.demo.modules.user.dtos.SearchUserRequestDTO;
import com.example.demo.modules.user.dtos.UserDTO;
import com.example.demo.utils.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/public")
public class PublicController {

    private final CommonMapper commonMapper;
    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final StringRedisTemplate redisTemplate;
    private final RoleService roleService;
    private final ConfigurationService configurationService;
    private final UserService userService;

    @GetMapping(value = "/health", produces = MediaType.TEXT_PLAIN_VALUE)
    public String check() {
        return "OK";
    }

    private List<UserDTO> createData(int total) {
        List<UserDTO> data = new ArrayList<>();
        OffsetDateTime now = OffsetDateTime.now();

        for (int i = 0; i < total; i++) {
            UserDTO user = new UserDTO();
            user.setId(RandomUtils.Insecure.generateTimeBasedUUID());
            user.setUsername(ConversionUtils.safeToString(RandomUtils.Insecure.generateTimeBasedUUID()));
            user.setEmail(ConversionUtils.safeToString(RandomUtils.Insecure.generateTimeBasedUUID()));
            user.setName(ConversionUtils.safeToString(RandomUtils.Insecure.generateTimeBasedUUID()));
            user.setPassword(ConversionUtils.safeToString(RandomUtils.Insecure.generateTimeBasedUUID()));
            user.setStatus(CommonStatus.ACTIVE);
            user.setCreatedBy(RandomUtils.Insecure.generateTimeBasedUUID());
            user.setUpdatedBy(RandomUtils.Insecure.generateTimeBasedUUID());
            user.setCreatedAt(now.plusSeconds(i));
            user.setUpdatedAt(now.plusMinutes(i));
            data.add(user);
        }
        return data;
    }

    @GetMapping("/export-excel")
    public String exportExcel(@RequestParam(required = false, defaultValue = "1000") Integer total) throws IOException {
        List<UserDTO> data = createData(total);
//        var requestDTO = SearchUserRequestDTO.builder()
//                .pageNumber(1)
//                .pageSize(1000000)
//                .build();
//        var result = userService.search(requestDTO, false);
//        var data = result.getItems();
        String exportPath = ".temp/excel-" + DateUtils.toEpochMicro() + ".xlsx";
        FastExcelHelper.Export.processTemplateWriteFile(new UserExportTemplate(data, true), exportPath);
        return "OK";
    }

    @GetMapping("/export-excel-batch")
    public String exportExcelBatch(@RequestParam(required = false, defaultValue = "1000") Long total) {
        long BATCH = 1000L;
        var requestDTO = SearchUserRequestDTO.builder()
                .pageNumber(1L)
                .pageSize(BATCH)
                .build();
        var result = userService.search(requestDTO, true);
        total = result.getTotalItems();
        try (Workbook workbook = new SXSSFWorkbook()) {
            UserExportTemplate template = new UserExportTemplate();
            template.setSkipHeader(false);
            for (int i = 0; i < total; i += BATCH) {
                result = userService.search(requestDTO, false);
                template.setBody(result.getItems());
                ExcelHelper.Export.processTemplate(template, workbook);
                template.setSkipHeader(true);
                requestDTO.setPageNumber(requestDTO.getPageNumber() + 1);
            }
            String exportPath = ".temp/excel-" + DateUtils.toEpochMicro() + ".xlsx";
            ExcelHelper.writeFile(workbook, exportPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "OK";
    }

    @GetMapping("/export-csv")
    public String exportCsv(@RequestParam(required = false, defaultValue = "1000") Integer total) {
        List<UserDTO> data = createData(total);
//        String exportPath = ".temp/csv-" + DateUtils.toEpochMicro() + ".csv";
//        CSVHelper.Export.processTemplateWriteFile(new UserExportTemplate(data, true), exportPath);
        return "OK";
    }

    @GetMapping("/import-excel")
    public String importExcel(@RequestParam String inputPath) {
        final int BATCH = 1000;
        List<UserDTO> items = new ArrayList<>();
        ExcelHelper.Import.processTemplate(new UserImportTemplate(x -> {
            if (items.size() >= BATCH) {
                items.clear();
            }
            items.add(x);
        }), inputPath);
        log.info("items {}", items.subList(0, Math.min(items.size(), 100)));
        return "OK";
    }

    @GetMapping("/import-csv")
    public String importCsv(@RequestParam String inputPath) {
        List<UserDTO> items = new ArrayList<>();
        CSVHelper.Import.processTemplate(new UserImportTemplate(items::add), inputPath);
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

    @GetMapping("/generate-users")
    public String generateUsers(@RequestParam(required = false, defaultValue = "1000") Integer total) {
        List<UserDTO> data = createData(total);
        List<User> users = data.stream().map(commonMapper::toEntity).toList();
        users = userRepository.saveAll(users);
        Set<Long> roleIds = new HashSet<>();
        roleIds.add(1L);
        for (User user : users) {
            roleService.addToUser(user.getId(), roleIds);
        }
        return "OK";
    }

    @GetMapping(value = "/i18n", produces = MediaType.TEXT_PLAIN_VALUE)
    public String testI18n(HttpServletRequest servletRequest, @RequestParam(required = false, defaultValue = "John Doe") String name) {
        return I18nHelper.getMessage("msg.hello", servletRequest.getLocale(), name);
    }

    @GetMapping(value = "/rand", produces = MediaType.TEXT_PLAIN_VALUE)
    public String rand(HttpServletRequest servletRequest, @RequestParam(required = false, defaultValue = "John Doe") String name) {
        long nano = DateUtils.toEpochNano(null);
        log.info("nano {}", nano);
        log.info("nano instant {}", DateUtils.toInstant(nano));
        long micro = DateUtils.toEpochMicro();
        log.info("micro {}", micro);
        log.info("micro instant {}", DateUtils.toInstant(micro));
        long milli = Instant.now().toEpochMilli();
        log.info("milli {}", milli);
        log.info("milli instant {}", DateUtils.toInstant(milli));
        long second = Instant.now().getEpochSecond();
        log.info("second {}", second);
        log.info("second instant {}", DateUtils.toInstant(second));

        log.info("epoch instant {}", DateUtils.toInstant(9999999999L));
        return "OK";
    }

    @GetMapping(value = "/jwt", produces = MediaType.TEXT_PLAIN_VALUE)
    public String jwttest(@RequestParam(required = false, defaultValue = "100") Integer total) {
        for (int i = 0; i < total; i++) {
            UUID uuid = RandomUtils.Secure.generateTimeBasedUUID();
            JWTPayload jwtPayload = jwtService.createActivateAccountJwt(uuid, false);
            jwtService.verify(jwtPayload.getValue());
        }
        return "OK";
    }

//    @PostMapping(value = "/import-excel-usage", produces = MediaType.TEXT_PLAIN_VALUE)
//    public String importExcelUsage(@RequestParam(name = "file", required = false) MultipartFile file) {
//        try {
//            log.info("{} {}", file.getInputStream().readAllBytes().length, file.getSize());
//            List<UserDTO> userDTOS = ExcelHelper.Import.processTemplate(new UserImportTemplate(), file);
//            file.getInputStream().close();
//            log.info("{} {}", file.getInputStream().readAllBytes().length, file.getSize());
//            Thread.sleep(3000);
//        } catch (Exception e) {
//            log.error("sleep", e);
//        }
//        return "OK";
//    }

    @GetMapping(value = "/bench", produces = MediaType.TEXT_PLAIN_VALUE)
    public String bench() {
        try {
            for (int i = 0; i < 1_000_000; i++) {
//                ZonedDateTime.now();
//                OffsetDateTime.now();
//                LocalDateTime.now();
//                Instant.now();
//                ZonedDateTime.now().toString();
//                OffsetDateTime.now().toString();
//                LocalDateTime.now().toString();
//                Instant.now().toString();
//                ZonedDateTime.now().format(DateUtils.Formatter.ID);
//                OffsetDateTime.now().format(DateUtils.Formatter.ID);
//                LocalDateTime.now().format(DateUtils.Formatter.ID);
//                DateUtils.toEpochMicro();
//                ConversionUtils.toString(DateUtils.toEpochMicro());
//                RandomUtils.generateUUID();
//                RandomUtils.generateTimeBasedUUID();
//                UUID.randomUUID();
//                RandomUtils.generateUUID().toString();
//                RandomUtils.generateTimeBasedUUID().toString();
//                UUID.randomUUID().toString();
                RandomUtils.Secure.generateUUID();
                RandomUtils.Insecure.generateUUID();
                RandomUtils.Insecure.generateString(16);
                RandomUtils.Secure.generateString(16);
                RandomUtils.Insecure.generateOTP(16);
                RandomUtils.Secure.generateOTP(16);
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return "OK";
    }

    @GetMapping(value = "/redis", produces = MediaType.TEXT_PLAIN_VALUE)
    public String redis() {
        try {
            redisTemplate.opsForValue().set("allo", RandomUtils.Secure.generateTimeBasedUUID().toString());

            log.info("redis: {}", redisTemplate.opsForValue().get("allo"));
        } catch (Exception e) {
            log.error("", e);
        }
        return "OK";
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<CommonResponse<ConfigurationDTO>> findOneByCode(@PathVariable String code) {
        try {
            var result = configurationService.findOneByCodeOrThrow(code);
            return ResponseEntity.ok(new CommonResponse<>(result));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @GetMapping("/rand")
    public Object testRand() {
        Map<String, Object> result = new HashMap<>();
        result.put("INRAN", RandomUtils.Insecure.generateString(16));
        result.put("RAN", RandomUtils.Secure.generateString(16));
        result.put("INOTP", RandomUtils.Insecure.generateOTP(6));
        result.put("OTP", RandomUtils.Secure.generateOTP(6));
        result.put("UUID", RandomUtils.Secure.generateUUID());
        result.put("IUUID", RandomUtils.Insecure.generateUUID());
        result.put("TUUID", RandomUtils.Secure.generateTimeBasedUUID());
        result.put("ITUUID", RandomUtils.Insecure.generateTimeBasedUUID());
        return result;
    }

    @GetMapping(value = "/totp", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object totp() {
        return TOTPHelper.generateSecret();
    }

}
