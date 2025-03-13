package com.example.demo.common.controller;

import com.example.demo.common.constant.CommonStatus;
import com.example.demo.common.constant.PermissionCode;
import com.example.demo.common.dto.CommonResponse;
import com.example.demo.common.mapper.CommonMapper;
import com.example.demo.common.util.*;
import com.example.demo.common.util.io.CSVHelper;
import com.example.demo.common.util.io.ExcelHelper;
import com.example.demo.common.util.io.FastExcelHelper;
import com.example.demo.common.util.io.ImageHelper;
import com.example.demo.module.configuration.ConfigurationService;
import com.example.demo.module.configuration.dto.ConfigurationDTO;
import com.example.demo.module.jwt.JWTService;
import com.example.demo.module.jwt.dto.JWTPayload;
import com.example.demo.module.role.RoleService;
import com.example.demo.module.user.User;
import com.example.demo.module.user.UserRepository;
import com.example.demo.module.user.UserService;
import com.example.demo.module.user.dto.SearchUserRequestDTO;
import com.example.demo.module.user.dto.UserDTO;
import com.example.demo.module.user.dto.UserExportTemplate;
import com.example.demo.module.user.dto.UserImportTemplate;
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
        Instant now = Instant.now();

        for (int i = 0; i < total; i++) {
            UserDTO user = new UserDTO();
            user.setId(RandomUtils.Insecure.generateTimeBasedUUID());
            user.setUsername(ConversionUtils.safeToString(RandomUtils.Insecure.generateTimeBasedUUID()));
            user.setEmail(ConversionUtils.safeToString(RandomUtils.Insecure.generateTimeBasedUUID()));
            user.setName(ConversionUtils.safeToString(RandomUtils.Insecure.generateTimeBasedUUID()));
            user.setPassword(ConversionUtils.safeToString(RandomUtils.Insecure.generateTimeBasedUUID()));
            user.setStatus(CommonStatus.ACTIVE);
            user.setCreatedAt(now.plusSeconds(i));
            user.setUpdatedAt(now.plusSeconds(i * 60L));
            data.add(user);
        }
        return data;
    }

    @GetMapping("/export-excel")
    public String exportExcel(@RequestParam(required = false, defaultValue = "1000") Integer total,
                              @RequestParam(required = false, defaultValue = "false") Boolean fast) throws IOException {
        List<UserDTO> data = createData(total);
//        var requestDTO = SearchUserRequestDTO.builder()
//                .pageNumber(1)
//                .pageSize(1000000)
//                .build();
//        var result = userService.search(requestDTO, false);
//        var data = result.getItems();
        String exportPath = ".temp/excel-" + DateUtils.currentEpochMicros() + ".xlsx";
        if (fast) {
            FastExcelHelper.Export.processTemplateWriteFile(UserExportTemplate.builder().body(data).skipHeader(false).build(), exportPath);
        } else {
            ExcelHelper.Export.processTemplateWriteFile(UserExportTemplate.builder().body(data).skipHeader(false).build(), exportPath);
        }
        return "OK";
    }

    @GetMapping("/export-excel-batch")
    public String exportExcelBatch(@RequestParam(required = false, defaultValue = "1000") Long total) {
        long BATCH = 1000L;
        var requestDTO = SearchUserRequestDTO.builder()
                .pageNumber(1L)
                .pageSize(BATCH)
                .build();
        total = userService.search(requestDTO, true).getTotalItems();
        try (Workbook workbook = new SXSSFWorkbook()) {
            for (int i = 0; i < total; i += BATCH) {
                var result = userService.search(requestDTO, false);
                UserExportTemplate template = UserExportTemplate.builder().body(result.getItems()).skipHeader(i > 0).build();
                ExcelHelper.Export.processTemplate(template, workbook);
                requestDTO.setPageNumber(requestDTO.getPageNumber() + 1);
            }
            String exportPath = ".temp/excel-" + DateUtils.currentEpochMicros() + ".xlsx";
            ExcelHelper.writeFile(workbook, exportPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "OK";
    }

    @GetMapping("/export-csv")
    public String exportCsv(@RequestParam(required = false, defaultValue = "1000") Integer total) {
        List<UserDTO> data = createData(total);
        String exportPath = ".temp/csv-" + DateUtils.currentEpochMicros() + ".csv";
        CSVHelper.Export.processTemplateWriteFile(new UserExportTemplate(data, true), exportPath);
        return "OK";
    }

    @GetMapping("/import-excel")
    public String importExcel(@RequestParam String inputPath,
                              @RequestParam(required = false, defaultValue = "false") Boolean fast) {
        final int BATCH = 1000;
        List<UserDTO> items = new ArrayList<>();
        if (fast) {
            FastExcelHelper.Import.processTemplate(new UserImportTemplate(x -> {
                items.add(x);
            }), inputPath);
        } else {
            ExcelHelper.Import.processTemplate(new UserImportTemplate(x -> {
                items.add(x);
            }), inputPath);
        }
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
        Set<UUID> roleIds = new HashSet<>();
        roleIds.add(RandomUtils.Secure.generateTimeBasedUUID());
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
        long nano = DateUtils.toEpochNanos(null);
        log.info("nano {}", nano);
        log.info("nano instant {}", DateUtils.toInstant(nano));
        long micro = DateUtils.currentEpochMicros();
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
                RandomUtils.Secure.generateUUID().toString();
                RandomUtils.Insecure.generateUUID().toString();
                RandomUtils.Secure.generateTimeBasedUUID().toString();
                RandomUtils.Insecure.generateTimeBasedUUID().toString();
                RandomUtils.Secure.generateString(16);
                RandomUtils.Insecure.generateString(16);
                RandomUtils.Secure.generateHexString(16);
                RandomUtils.Insecure.generateHexString(16);
                RandomUtils.Secure.generateOTP(16);
                RandomUtils.Insecure.generateOTP(16);
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
        result.put("Secure.generateHexString", RandomUtils.Secure.generateHexString(16));
        result.put("Insecure.generateHexString", RandomUtils.Insecure.generateHexString(16));
        result.put("Secure.generateOTP", RandomUtils.Secure.generateOTP(16));
        result.put("Insecure.generateOTP", RandomUtils.Insecure.generateOTP(16));
        result.put("Secure.generateUUID", RandomUtils.Secure.generateUUID());
        result.put("Insecure.generateUUID", RandomUtils.Insecure.generateUUID());
        result.put("Secure.generateTimeBasedUUID", RandomUtils.Secure.generateTimeBasedUUID());
        result.put("Insecure.generateTimeBasedUUID", RandomUtils.Insecure.generateTimeBasedUUID());
        result.put("Secure.generateString", RandomUtils.Secure.generateString(16));
        result.put("Insecure.generateString", RandomUtils.Insecure.generateString(16));
        return result;
    }

    @GetMapping(value = "/totp", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object totp() {
        return TOTPHelper.generateSecret();
    }

    @GetMapping(value = "/image/compress", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object image(
            @RequestParam String inputPath,
            @RequestParam(required = false) Integer width,
            @RequestParam(required = false) Integer height,
            @RequestParam(required = false) String format,
            @RequestParam(required = false) Float quality) throws IOException {
        ImageHelper.compressImageWriteFile(inputPath, ".temp/compressed-" + DateUtils.currentEpochMillis() + ".jpg",
                ImageHelper.Options.builder().width(width).height(height).quality(quality).format(format).build());
        return "OK";
    }

    public List<Integer> getSetBits(long n) {
        List<Integer> positions = new ArrayList<>();
        int pos = 0;
        while (n != 0) {
            if ((n & 1) == 1) {
                positions.add(pos);
            }
            n >>= 1;
            pos++;
        }
        return positions;
    }

    public List<String> getSetBitsD(long n) {
        List<String> positions = new ArrayList<>();
        int pos = 0;
        while (n != 0) {
            if ((n & 1) == 1) {
                positions.add(PermissionCode.fromIndex(pos));
            }
            n >>= 1;
            pos++;
        }
        return positions;
    }

    public List<String> toPerms(long n) {
        return getSetBitsD(n);
    }

    public List<String> toPerms(List<Integer> ints) {
        return PermissionCode.fromIndexes(ints);
    }

    @GetMapping(value = "/bits", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object image(@RequestParam Long num) throws IOException {
        List<Integer> ints = getSetBits(num);
        for (int i = 0; i < 1_000_000; i++) {
            toPerms(num);
            toPerms(ints);
        }
        return "OK";
    }

}
