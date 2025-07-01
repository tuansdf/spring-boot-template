package com.example.sbt.core.controller;

import com.example.sbt.shared.constant.FileType;
import com.example.sbt.common.util.*;
import com.example.sbt.core.constant.CommonStatus;
import com.example.sbt.core.constant.PermissionCode;
import com.example.sbt.core.dto.RequestContext;
import com.example.sbt.core.exception.CustomException;
import com.example.sbt.core.exception.ValidationException;
import com.example.sbt.core.helper.LocaleHelper;
import com.example.sbt.module.file.service.FileObjectService;
import com.example.sbt.module.file.service.UploadFileService;
import com.example.sbt.module.notification.dto.SendNotificationRequest;
import com.example.sbt.module.notification.service.SendNotificationService;
import com.example.sbt.module.user.dto.UserDTO;
import com.example.sbt.shared.util.*;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.*;
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

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Secured(PermissionCode.SYSTEM_ADMIN)
@RestController
@RequestMapping("/test")
public class TestController {

    private final LocaleHelper localeHelper;
    private final StringRedisTemplate redisTemplate;
    private final SendNotificationService sendNotificationService;
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
            ExcelUtils.setRowCellValues(sheet, 0, header);
            int idx = 1;
            for (var item : data) {
                ExcelUtils.setRowCellValues(sheet, idx, Arrays.asList(idx, item.getId(), item.getUsername(), item.getEmail(), item.getName(), item.getStatus(), item.getCreatedAt(), item.getUpdatedAt()));
                idx++;
            }
            ExcelUtils.writeFile(workbook, exportPath);
        }
        return "OK";
    }

    @GetMapping("/csv/export")
    public String testExportCsv(@RequestParam(required = false, defaultValue = "1000") Integer total) {
        List<UserDTO> data = createData(total);
        String exportPath = ".temp/csv-" + DateUtils.currentEpochMicros() + ".csv";
        try (CSVWriter writer = new CSVWriter(new FileWriter(exportPath))) {
            String[] header = {"Order", "ID", "Username", "Email", "Name", "Status", "Created At", "Updated At", "Temp", "Temp", "Temp", "Temp"};
            writer.writeNext(header);
            int idx = 1;
            for (var item : data) {
                writer.writeNext(new String[]{
                        ConversionUtils.toString(idx),
                        ConversionUtils.toString(item.getId()),
                        item.getUsername(),
                        item.getEmail(),
                        item.getName(),
                        item.getStatus(),
                        ConversionUtils.toString(item.getCreatedAt()),
                        ConversionUtils.toString(item.getUpdatedAt())});
                idx++;
            }
        } catch (IOException e) {
            log.error("test import ", e);
        }
        return "OK";
    }

    @GetMapping("/excel/import")
    public String testImportExcel(@RequestParam String filePath) {
        List<UserDTO> items = new ArrayList<>();
        try (Workbook workbook = ExcelUtils.toWorkbook(filePath)) {
            if (workbook == null) return null;
            List<Object> header = List.of("Order", "ID", "Username", "Email", "Name", "Status", "Created At", "Updated At");
            Sheet sheet = workbook.getSheetAt(0);
            boolean isHeader = true;
            for (var row : sheet) {
                List<Object> data = ExcelUtils.getRowCellValues(row);
                if (isHeader) {
                    if (!ListUtils.isEqualList(header, data)) {
                        throw new CustomException("Invalid template");
                    }
                    isHeader = false;
                    continue;
                }
                UserDTO temp = UserDTO.builder()
                        .id(ConversionUtils.toUUID(CommonUtils.get(data, 1)))
                        .username(ConversionUtils.toString(CommonUtils.get(data, 2)))
                        .email(ConversionUtils.toString(CommonUtils.get(data, 3)))
                        .name(ConversionUtils.toString(CommonUtils.get(data, 4)))
                        .status(ConversionUtils.toString(CommonUtils.get(data, 5)))
                        .createdAt(DateUtils.toInstant(CommonUtils.get(data, 6), DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                        .updatedAt(DateUtils.toInstant(CommonUtils.get(data, 7), DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                        .build();
                items.add(temp);
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
        try (CSVReader reader = new CSVReader(Files.newBufferedReader(Paths.get(filePath)))) {
            String[] header = {"Order", "ID", "Username", "Email", "Name", "Status", "Created At", "Updated At", "Temp", "Temp", "Temp", "Temp"};
            boolean isHeader = true;
            for (var row : reader) {
                if (isHeader) {
                    if (!Arrays.equals(header, row)) {
                        throw new CustomException("Invalid template");
                    }
                    isHeader = false;
                    continue;
                }
                UserDTO temp = UserDTO.builder()
                        .id(ConversionUtils.toUUID(CommonUtils.get(row, 1)))
                        .username(ConversionUtils.toString(CommonUtils.get(row, 2)))
                        .email(ConversionUtils.toString(CommonUtils.get(row, 3)))
                        .name(ConversionUtils.toString(CommonUtils.get(row, 4)))
                        .status(ConversionUtils.toString(CommonUtils.get(row, 5)))
                        .createdAt(DateUtils.toInstant(CommonUtils.get(row, 6), DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                        .updatedAt(DateUtils.toInstant(CommonUtils.get(row, 7), DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                        .build();
                items.add(temp);
            }
        } catch (Exception e) {
            log.error("test import ", e);
        }
        log.info("items {}", items.subList(0, Math.min(items.size(), 100)));
        return "OK";
    }

    @GetMapping(value = "/i18n", produces = MediaType.TEXT_PLAIN_VALUE)
    public String testI18n(@RequestParam String key) {
        return localeHelper.getMessage(key);
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
        return TOTPUtils.generateSecret();
    }

    @GetMapping(value = "/totp/verify", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object testTotpVerify(@RequestParam String secret) {
        for (int i = 0; i < 1_000_000; i++) {
            TOTPUtils.verify("123456", secret);
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

    @GetMapping(value = "/notification/send", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object testNotificationSend(@RequestParam String token, @RequestParam(required = false) String topic) throws FirebaseMessagingException {
        sendNotificationService.sendAsync(SendNotificationRequest.builder()
                .title("notification title")
                .body("notification content")
                .topic(topic)
                .tokens(Collections.singletonList(token))
                .build());
        return "OK";
    }

    @GetMapping(value = "/notification/subscribe-topic", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object testNotificationSubscribeTopic(@RequestParam String token, @RequestParam String topic) throws FirebaseMessagingException {
        sendNotificationService.subscribeTopicAsync(SendNotificationRequest.builder()
                .topic(topic)
                .tokens(Collections.singletonList(token))
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
        return fileObjectService.createPendingUpload("", extension);
    }

    @GetMapping(value = "/s3/delete", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object testS3Delete(@RequestParam List<UUID> ids) {
        fileObjectService.deleteFilesByIds(ids, RequestContext.get().getUserId());
        return "OK";
    }

    @GetMapping(value = "/s3/get", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object testS3GetFile(@RequestParam String filePath) {
        FileUtils.writeFile(uploadFileService.getFile(filePath), ".temp/" + FilenameUtils.getBaseName(filePath) + "-" + DateUtils.currentEpochMillis() + "." + FilenameUtils.getExtension(filePath));
        return "OK";
    }

    @GetMapping(value = "/text/clean-file-name", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object testCleanFileName(@RequestBody TestBody body) {
        return FileUtils.cleanFileName(body.getText());
    }

    @GetMapping(value = "/text/tostring", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object testToString(@RequestBody TestBody body) {
        log.info("body: {}", body);
        return "OK";
    }

    @GetMapping(value = "/exception/validation", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object testValidationException() {
        throw new ValidationException(List.of("Invalid A", "Invalid B", "Invalid C"));
    }

    @GetMapping(value = "/files/validate-type", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object testFilesValidateType(@RequestParam MultipartFile file, @RequestParam String fileType) {
        return ConversionUtils.toString(FileUtils.validateFileType(file, Collections.singletonList(FileType.fromExtension(fileType))));
    }

    @GetMapping(value = "/files/validate-type/bench", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object testFilesValidateTypeBench(@RequestParam MultipartFile file) {
        List<FileType> fileTypes = List.of(FileType.PNG, FileType.WEBP, FileType.JPEG);
        for (int i = 0; i < 10_000; i++) {
            FileUtils.validateFileType(file, fileTypes);
        }
        return "OK";
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TestBody {
        private String text;
        private Map<String, Long> mapStringLong;
    }

}
