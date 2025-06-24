package com.example.sbt.common.controller;

import com.example.sbt.common.constant.CommonStatus;
import com.example.sbt.common.constant.FileType;
import com.example.sbt.common.constant.PermissionCode;
import com.example.sbt.common.exception.CustomException;
import com.example.sbt.common.exception.ValidationException;
import com.example.sbt.common.util.*;
import com.example.sbt.common.util.io.CSVHelper;
import com.example.sbt.common.util.io.ExcelHelper;
import com.example.sbt.common.util.io.FileUtils;
import com.example.sbt.common.util.io.ImageUtils;
import com.example.sbt.module.file.service.FileObjectService;
import com.example.sbt.module.file.service.UploadFileService;
import com.example.sbt.module.user.dto.UserDTO;
import com.google.common.collect.Lists;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Secured({PermissionCode.SYSTEM_ADMIN})
@RestController
@RequestMapping("/testing")
public class PrivateController {

    private final StringRedisTemplate redisTemplate;
    private final FirebaseMessaging firebaseMessaging;
    private final FileObjectService fileObjectService;
    private final UploadFileService uploadFileService;

    @GetMapping("/health")
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

    @GetMapping("/excel/export")
    public String testExportExcel(@RequestParam(required = false, defaultValue = "1000") Integer total) throws IOException {
        List<UserDTO> data = createData(total);
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

    @GetMapping("/csv/export")
    public String testExportCsv(@RequestParam(required = false, defaultValue = "1000") Integer total) {
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

    @GetMapping("/excel/import")
    public String testImportExcel(@RequestParam String filePath) {
        List<UserDTO> items = new ArrayList<>();
        try (Workbook workbook = ExcelHelper.toWorkbook(filePath)) {
            if (workbook == null) return null;
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
                    List<Object> data = CommonUtils.padRight(ExcelHelper.getRowCellValues(row), rowSize);
                    UserDTO temp = UserDTO.builder()
                            .id(ConversionUtils.toUUID(data.get(1)))
                            .username(ConversionUtils.toString(data.get(2)))
                            .email(ConversionUtils.toString(data.get(3)))
                            .name(ConversionUtils.toString(data.get(4)))
                            .status(ConversionUtils.toString(data.get(5)))
                            .createdAt(DateUtils.toInstant(data.get(6), DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                            .updatedAt(DateUtils.toInstant(data.get(7), DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                            .build();
                    items.add(temp);
                } finally {
                    idx++;
                }
            }
        } catch (Exception e) {
            log.error("test import ", e);
        }
        log.info("items: {}", items.subList(0, Math.min(items.size(), 100)));
        return "OK";
    }

    @GetMapping("/csv/import")
    public String testImportCsv(@RequestParam String filePath) {
        List<UserDTO> items = new ArrayList<>();
        CSVHelper.Import.processTemplate(filePath, csvParser -> {
            List<String> header = List.of("Order", "ID", "Username", "Email", "Name", "Status", "Created At", "Updated At");
            int rowSize = header.size();
            if (!ListUtils.isEqualList(header, csvParser.getHeaderNames())) {
                throw new CustomException("Invalid template");
            }
            for (var item : csvParser) {
                List<String> data = CommonUtils.padRight(item.stream().toList(), rowSize);
                UserDTO temp = UserDTO.builder()
                        .id(ConversionUtils.toUUID(data.get(1)))
                        .username(ConversionUtils.toString(data.get(2)))
                        .email(ConversionUtils.toString(data.get(3)))
                        .name(ConversionUtils.toString(data.get(4)))
                        .status(ConversionUtils.toString(data.get(5)))
                        .createdAt(DateUtils.toInstant(data.get(6), DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                        .updatedAt(DateUtils.toInstant(data.get(7), DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                        .build();
                items.add(temp);
            }
        });
        log.info("items {}", items.subList(0, Math.min(items.size(), 100)));
        return "OK";
    }

    @GetMapping(value = "/i18n", produces = MediaType.TEXT_PLAIN_VALUE)
    public String testI18n(@RequestParam String key) {
        return LocaleHelper.getMessage(key);
    }

    @GetMapping(value = "/redis", produces = MediaType.TEXT_PLAIN_VALUE)
    public String testRedis() {
        String key = "TEST_REDIS_KEY_" + DateUtils.currentEpochMillis();
        redisTemplate.opsForValue().set(key, RandomUtils.Secure.generateTimeBasedUUID().toString());
        log.info("{}: {}", key, redisTemplate.opsForValue().get(key));
        return "OK";
    }

    @GetMapping("/random")
    public Object testRandom() {
        Map<String, Object> result = new HashMap<>();
        result.put("Secure.generateUUID", RandomUtils.Secure.generateUUID());
        result.put("Insecure.generateUUID", RandomUtils.Insecure.generateUUID());
        result.put("Secure.generateTimeBasedUUID", RandomUtils.Secure.generateTimeBasedUUID());
        result.put("Insecure.generateTimeBasedUUID", RandomUtils.Insecure.generateTimeBasedUUID());
        result.put("Secure.generateString(4)", RandomUtils.Secure.generateString(4));
        result.put("Insecure.generateString(4)", RandomUtils.Insecure.generateString(4));
        result.put("Secure.generateString(8)", RandomUtils.Secure.generateString(8));
        result.put("Insecure.generateString(8)", RandomUtils.Insecure.generateString(8));
        result.put("Secure.generateString(16)", RandomUtils.Secure.generateString(16));
        result.put("Insecure.generateString(16)", RandomUtils.Insecure.generateString(16));
        result.put("Secure.generateHexString(4)", RandomUtils.Secure.generateHexString(4));
        result.put("Insecure.generateHexString(4)", RandomUtils.Insecure.generateHexString(4));
        result.put("Secure.generateHexString(8)", RandomUtils.Secure.generateHexString(8));
        result.put("Insecure.generateHexString(8)", RandomUtils.Insecure.generateHexString(8));
        result.put("Secure.generateHexString(16)", RandomUtils.Secure.generateHexString(16));
        result.put("Insecure.generateHexString(16)", RandomUtils.Insecure.generateHexString(16));
        result.put("Secure.generateOTP(8)", RandomUtils.Secure.generateOTP(8));
        result.put("Insecure.generateOTP(8)", RandomUtils.Insecure.generateOTP(8));
        result.put("Secure.generateOTP(16)", RandomUtils.Secure.generateOTP(16));
        result.put("Insecure.generateOTP(16)", RandomUtils.Insecure.generateOTP(16));
        result.put("DateUtils.currentEpochMillis", DateUtils.currentEpochMillis());
        result.put("DateUtils.currentEpochMicros", DateUtils.currentEpochMicros());
        result.put("DateUtils.currentEpochNanos", DateUtils.currentEpochNanos());
        return result;
    }

    @GetMapping(value = "/random/bench", produces = MediaType.TEXT_PLAIN_VALUE)
    public String bench() {
        for (int i = 0; i < 1_000_000; i++) {
            UUID.randomUUID();
            RandomUtils.Secure.generateUUID();
            RandomUtils.Secure.generateUUID().toString();
            RandomUtils.Insecure.generateUUID();
            RandomUtils.Insecure.generateUUID().toString();
            RandomUtils.Secure.generateTimeBasedUUID();
            RandomUtils.Secure.generateTimeBasedUUID().toString();
            RandomUtils.Insecure.generateTimeBasedUUID();
            RandomUtils.Insecure.generateTimeBasedUUID().toString();
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
            ConversionUtils.toString(DateUtils.currentEpochMillis());
            ConversionUtils.toString(DateUtils.currentEpochMicros());
            ConversionUtils.toString(DateUtils.currentEpochNanos());
        }
        return "OK";
    }

    @GetMapping(value = "/totp/secret", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object testTotpSecret() {
        return TOTPHelper.generateSecret();
    }

    @GetMapping(value = "/totp/verify", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object testTotpVerify(@RequestParam String secret) {
        for (int i = 0; i < 1_000_000; i++) {
            TOTPHelper.verify("123456", secret);
        }
        return "OK";
    }

    @GetMapping(value = "/image/compress", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object image(
            @RequestParam String filePath,
            @RequestParam(required = false) Integer width,
            @RequestParam(required = false) Integer height,
            @RequestParam(required = false) String format,
            @RequestParam(required = false) Float quality) {
        ImageUtils.compressImageWriteFile(filePath, ".temp/compressed-" + DateUtils.currentEpochMillis() + ".jpg",
                ImageUtils.Options.builder().width(width).height(height).quality(quality).format(format).build());
        return "OK";
    }

    @GetMapping(value = "/push-notification", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object testPushNotification(@RequestParam String token) throws FirebaseMessagingException {
        firebaseMessaging.send(Message.builder()
                .setToken(token)
                .putData("hello", "world")
                .build());
        return "OK";
    }

    @GetMapping(value = "/s3/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object testS3Upload(@RequestParam MultipartFile file, @RequestParam(defaultValue = "") String filePath) throws IOException {
        return fileObjectService.uploadFile(file, filePath);
    }

    @GetMapping(value = "/s3/presigned", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object testS3Presigned(@RequestParam UUID id) {
        return fileObjectService.getFileById(id);
    }

    @GetMapping(value = "/s3/presigned/put", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object testS3PresignedPut(@RequestParam String extension) {
        return fileObjectService.createPendingFileUpload("", extension);
    }

    @GetMapping(value = "/s3/delete", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object testS3Delete(@RequestParam Set<UUID> ids) {
        fileObjectService.deleteFilesByIds(ids);
        return "OK";
    }

    @GetMapping(value = "/s3/get", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object testS3GetFile(@RequestParam String filePath) {
        FileUtils.writeFile(uploadFileService.getFile(filePath), ".temp/" + FilenameUtils.getBaseName(filePath) + "-" + DateUtils.currentEpochMillis() + "." + FilenameUtils.getExtension(filePath));
        return "OK";
    }

    @GetMapping(value = "/text/clean-file-name", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object testCleanFileName(@RequestBody TestBody body) {
        return FileUtils.cleanFileName(body.text());
    }

    @GetMapping(value = "/exception/validation", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object testValidationException() {
        throw new ValidationException(List.of("Invalid A", "Invalid B", "Invalid C"));
    }

    @GetMapping(value = "/files/validate-type", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object testFilesValidateType(@RequestParam MultipartFile file, @RequestParam String fileType) {
        return ConversionUtils.toString(FileUtils.validateFileType(file, Lists.newArrayList(FileType.fromExtension(fileType))));
    }

    @GetMapping(value = "/files/validate-type/bench", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object testFilesValidateTypeBench(@RequestParam MultipartFile file) {
        List<FileType> fileTypes = List.of(FileType.PNG, FileType.WEBP, FileType.JPEG);
        for (int i = 0; i < 10_000; i++) {
            FileUtils.validateFileType(file, fileTypes);
        }
        return "OK";
    }

    public record TestBody(String text) {
    }

}
