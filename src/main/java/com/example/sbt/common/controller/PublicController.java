package com.example.sbt.common.controller;

import com.example.sbt.common.constant.CommonStatus;
import com.example.sbt.common.constant.PermissionCode;
import com.example.sbt.common.dto.CommonResponse;
import com.example.sbt.common.exception.CustomException;
import com.example.sbt.common.mapper.CommonMapper;
import com.example.sbt.common.util.*;
import com.example.sbt.common.util.io.CSVHelper;
import com.example.sbt.common.util.io.ExcelHelper;
import com.example.sbt.common.util.io.ImageUtils;
import com.example.sbt.module.configuration.ConfigurationService;
import com.example.sbt.module.configuration.dto.ConfigurationDTO;
import com.example.sbt.module.file.FileObjectService;
import com.example.sbt.module.jwt.JWTService;
import com.example.sbt.module.jwt.dto.JWTPayload;
import com.example.sbt.module.role.RoleService;
import com.example.sbt.module.user.UserService;
import com.example.sbt.module.user.dto.SearchUserRequestDTO;
import com.example.sbt.module.user.dto.UserDTO;
import com.example.sbt.module.user.dto.UserExportTemplate;
import com.example.sbt.module.user.entity.User;
import com.example.sbt.module.user.repository.UserRepository;
import com.google.common.collect.Lists;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/pub/utils")
public class PublicController {

    private final CommonMapper commonMapper;
    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final StringRedisTemplate redisTemplate;
    private final RoleService roleService;
    private final ConfigurationService configurationService;
    private final UserService userService;
    private final FirebaseMessaging firebaseMessaging;
    private final FileObjectService fileObjectService;

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
    public String exportExcel(@RequestParam(required = false, defaultValue = "1000") Integer total) throws IOException {
        List<UserDTO> data = createData(total);
//        var requestDTO = SearchUserRequestDTO.builder()
//                .pageNumber(1)
//                .pageSize(1000000)
//                .build();
//        var result = userService.search(requestDTO, false);
//        var data = result.getItems();
        String exportPath = ".temp/excel-" + DateUtils.currentEpochMicros() + ".xlsx";
        try (Workbook workbook = new SXSSFWorkbook()) {
            Sheet sheet = workbook.createSheet();
            List<Object> header = List.of("Order", "ID", "Username", "Email", "Name", "Status", "Created At", "Updated At");
            ExcelHelper.setRowCellValues(sheet, 0, header);
            int idx = 1;
            for (var item : data) {
                ExcelHelper.setRowCellValues(sheet, idx, Lists.newArrayList(idx, item.getId(), item.getUsername(), item.getEmail(), item.getName(), item.getStatus(), item.getCreatedAt(), item.getUpdatedAt()));
                idx++;
            }
            ExcelHelper.writeFile(workbook, exportPath);
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
//                ExcelHelper.Export.processTemplate(template, workbook);
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
        CSVHelper.Export.processTemplateWriteFile(exportPath,
                List.of("Order", "ID", "Username", "Email", "Name", "Status", "Created At", "Updated At"),
                csvPrinter -> {
                    try {
                        int idx = 1;
                        for (var item : data) {
                            csvPrinter.printRecord(Lists.newArrayList(idx, item.getId(), item.getUsername(), item.getEmail(), item.getName(), item.getStatus(), item.getCreatedAt(), item.getUpdatedAt()));
                            idx++;
                        }
                    } catch (Exception ignored) {
                    }
                });
        return "OK";
    }

    @GetMapping("/import-excel")
    public String importExcel(@RequestParam String inputPath) {
        final int BATCH = 1000;
        List<UserDTO> items = new ArrayList<>();
        ExcelHelper.Import.processTemplate(inputPath, workbook -> {
            List<Object> header = List.of("Order", "ID", "Username", "Email", "Name", "Status", "Created At", "Updated At");
            int rowSize = header.size();
            Sheet sheet = workbook.getSheetAt(0);
            int idx = 0;
            for (var row : sheet) {
                try {
                    if (idx == 0) {
                        if (!ListUtils.isEqualList(header, ExcelHelper.getRowCellValues(row))) {
                            throw new CustomException("Invalid template");
                        }
                        continue;
                    }
                    List<Object> data = CommonUtils.rightPad(ExcelHelper.getRowCellValues(row), rowSize);
                    UserDTO temp = UserDTO.builder()
                            .id(ConversionUtils.toUUID(data.get(1)))
                            .username(ConversionUtils.toString(data.get(2)))
                            .email(ConversionUtils.toString(data.get(3)))
                            .name(ConversionUtils.toString(data.get(4)))
                            .status(ConversionUtils.toString(data.get(5)))
                            .build();
                    OffsetDateTime createdAt = DateUtils.toOffsetDateTime(data.get(6));
                    if (createdAt != null) {
                        temp.setCreatedAt(createdAt.toInstant());
                    }
                    OffsetDateTime updatedAt = DateUtils.toOffsetDateTime(data.get(7));
                    if (updatedAt != null) {
                        temp.setCreatedAt(updatedAt.toInstant());
                    }
                    items.add(temp);
                } finally {
                    idx++;
                }
            }
        });
        log.info("items {}", items.subList(0, Math.min(items.size(), 100)));
        return "OK";
    }

    @GetMapping("/import-csv")
    public String importCsv(@RequestParam String inputPath) {
        List<UserDTO> items = new ArrayList<>();
        CSVHelper.Import.processTemplate(inputPath, csvParser -> {
            List<String> header = List.of("Order", "ID", "Username", "Email", "Name", "Status", "Created At", "Updated At");
            int rowSize = header.size();
            if (!ListUtils.isEqualList(header, csvParser.getHeaderNames())) {
                throw new CustomException("Invalid template");
            }
            for (var item : csvParser) {
                List<String> data = CommonUtils.rightPad(item.stream().toList(), rowSize);
                UserDTO temp = UserDTO.builder()
                        .id(ConversionUtils.toUUID(data.get(1)))
                        .username(ConversionUtils.toString(data.get(2)))
                        .email(ConversionUtils.toString(data.get(3)))
                        .name(ConversionUtils.toString(data.get(4)))
                        .status(ConversionUtils.toString(data.get(5)))
                        .build();
                OffsetDateTime createdAt = DateUtils.toOffsetDateTime(data.get(6));
                if (createdAt != null) {
                    temp.setCreatedAt(createdAt.toInstant());
                }
                OffsetDateTime updatedAt = DateUtils.toOffsetDateTime(data.get(7));
                if (updatedAt != null) {
                    temp.setCreatedAt(updatedAt.toInstant());
                }
                items.add(temp);
            }
        });
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
            roleService.setUserRoles(user.getId(), roleIds);
        }
        return "OK";
    }

    @GetMapping(value = "/i18n", produces = MediaType.TEXT_PLAIN_VALUE)
    public String testI18n(HttpServletRequest servletRequest, @RequestParam(required = false, defaultValue = "John Doe") String name) {
        return LocaleHelper.getMessage("msg.hello", servletRequest.getLocale(), name);
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
                RandomUtils.Secure.generateUUID();
                RandomUtils.Insecure.generateUUID();
                RandomUtils.Secure.generateTimeBasedUUID();
                RandomUtils.Insecure.generateTimeBasedUUID();
                RandomUtils.Secure.generateString(8);
                RandomUtils.Insecure.generateString(8);
                RandomUtils.Secure.generateString(16);
                RandomUtils.Insecure.generateString(16);
                RandomUtils.Secure.generateHexString(8);
                RandomUtils.Insecure.generateHexString(8);
                RandomUtils.Secure.generateHexString(16);
                RandomUtils.Insecure.generateHexString(16);
                RandomUtils.Secure.generateOTP(8);
                RandomUtils.Insecure.generateOTP(8);
                RandomUtils.Secure.generateOTP(16);
                RandomUtils.Insecure.generateOTP(16);
                DateUtils.currentEpochMillis();
                DateUtils.currentEpochMicros();
                DateUtils.currentEpochNanos();
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
        String secret = "B4JJYHMIGX2NZWAZWSX243BJH4BY7MMV";
        for (int i = 0; i < 1_000_000; i++) {
            TOTPHelper.verify("123456", secret);
        }
        return TOTPHelper.generateSecret();
    }

    @GetMapping(value = "/image/compress", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object image(
            @RequestParam String inputPath,
            @RequestParam(required = false) Integer width,
            @RequestParam(required = false) Integer height,
            @RequestParam(required = false) String format,
            @RequestParam(required = false) Float quality) throws IOException {
        ImageUtils.compressImageWriteFile(inputPath, ".temp/compressed-" + DateUtils.currentEpochMillis() + ".jpg",
                ImageUtils.Options.builder().width(width).height(height).quality(quality).format(format).build());
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

    @GetMapping(value = "/perms", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object perms() throws IOException {
        List<String> STRINGS = List.of(
                PermissionCode.SYSTEM_ADMIN,
                PermissionCode.READ_USER,
                PermissionCode.CREATE_USER,
                PermissionCode.UPDATE_USER,
                PermissionCode.DELETE_USER);
        for (int i = 0; i < 1_000_000; i++) {
            PermissionCode.toIndexes(STRINGS);
        }
        return "OK";
    }

    public void doNothingNested() {
    }

    public void doNothing() {
        doNothingNested();
    }

    public void doManyNothing() {
        doNothing();
        doNothing();
        doNothing();
        doNothing();
        doNothing();
        doNothing();
        doNothing();
        doNothing();
        doNothing();
        doNothing();
    }

    @GetMapping(value = "/logging-aspect", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object loggingAspect() throws IOException {
        for (int i = 0; i < 10_000_000; i++) {
            doManyNothing();
        }
        return "OK";
    }

    @GetMapping(value = "/test-firebase", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object testFirebase(@RequestParam String token) throws IOException, FirebaseMessagingException {
        firebaseMessaging.send(Message.builder()
                .setToken(token)
                .putData("hello", "world")
                .build());
        return "OK";
    }

    @GetMapping(value = "/s3-upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object testS3Upload(@RequestParam MultipartFile file, @RequestParam String filePath) throws IOException, FirebaseMessagingException {
        return fileObjectService.uploadImage(file, filePath);
    }

}
