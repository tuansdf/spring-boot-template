package com.example.sbt.module.user.service;

import com.example.sbt.core.constant.PermissionCode;
import com.example.sbt.core.constant.ResultSetName;
import com.example.sbt.core.dto.PaginationData;
import com.example.sbt.core.exception.CustomException;
import com.example.sbt.core.helper.AuthHelper;
import com.example.sbt.core.helper.CommonHelper;
import com.example.sbt.core.helper.SQLHelper;
import com.example.sbt.event.publisher.ExportUserEventPublisher;
import com.example.sbt.module.backgroundtask.constant.BackgroundTaskStatus;
import com.example.sbt.module.backgroundtask.constant.BackgroundTaskType;
import com.example.sbt.module.backgroundtask.dto.BackgroundTaskDTO;
import com.example.sbt.module.backgroundtask.service.BackgroundTaskService;
import com.example.sbt.module.file.dto.FileObjectDTO;
import com.example.sbt.module.file.service.FileObjectService;
import com.example.sbt.module.role.service.RoleService;
import com.example.sbt.module.user.dto.SearchUserRequestDTO;
import com.example.sbt.module.user.dto.UserDTO;
import com.example.sbt.module.user.entity.User;
import com.example.sbt.module.user.mapper.UserMapper;
import com.example.sbt.module.user.repository.UserRepository;
import com.example.sbt.shared.constant.FileType;
import com.example.sbt.shared.util.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class UserServiceImpl implements UserService {
    private static final long MAX_ITEMS = 1000000L;

    private final CommonHelper commonHelper;
    private final SQLHelper sqlHelper;
    private final AuthHelper authHelper;
    private final UserMapper userMapper;
    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final RoleService roleService;
    private final FileObjectService fileObjectService;
    private final ExportUserEventPublisher exportUserEventPublisher;
    private final BackgroundTaskService backgroundTaskService;

    private PaginationData<UserDTO> executeSearch(SearchUserRequestDTO requestDTO, boolean isCount, boolean isAll) {
        requestDTO.setOrderBy(CommonUtils.inListOrNull(requestDTO.getOrderBy(), List.of("username", "email")));
        requestDTO.setOrderDirection(CommonUtils.inListOrNull(requestDTO.getOrderDirection(), List.of("asc", "desc")));
        PaginationData<UserDTO> result = sqlHelper.initData(requestDTO.getPageNumber(), requestDTO.getPageSize());
        Map<String, Object> params = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        if (!isCount) {
            builder.append(" select u.id, u.username, u.email, u.name, u.is_verified, u.is_otp_enabled, u.status, u.created_at, u.updated_at, ");
            builder.append(" string_agg(distinct(r.code), ',') as roles, ");
            builder.append(" string_agg(distinct(p.code), ',') as permissions ");
            builder.append(" from _user u ");
            builder.append(" inner join ( ");
        }
        {
            if (isCount) {
                builder.append(" select count(*) ");
            } else {
                builder.append(" select u.id ");
            }
            builder.append(" from _user u ");
            builder.append(" where 1=1 ");
            if (requestDTO.getId() != null) {
                builder.append(" and u.id = :id ");
                params.put("id", requestDTO.getId());
            }
            if (StringUtils.isNotBlank(requestDTO.getUsername())) {
                builder.append(" and u.username = :username ");
                params.put("username", requestDTO.getUsername());
            }
            if (StringUtils.isNotBlank(requestDTO.getEmail())) {
                builder.append(" and u.email = :email ");
                params.put("email", requestDTO.getEmail());
            }
            if (requestDTO.getIdFrom() != null) {
                builder.append(" and u.id > :idFrom ");
                params.put("idFrom", requestDTO.getIdFrom());
            }
            if (StringUtils.isNotBlank(requestDTO.getUsernameFrom())) {
                builder.append(" and u.username > :usernameFrom ");
                params.put("usernameFrom", requestDTO.getUsernameFrom());
            }
            if (StringUtils.isNotBlank(requestDTO.getEmailFrom())) {
                builder.append(" and u.email > :emailFrom ");
                params.put("emailFrom", requestDTO.getEmailFrom());
            }
            if (StringUtils.isNotBlank(requestDTO.getStatus())) {
                builder.append(" and u.status = :status ");
                params.put("status", requestDTO.getStatus());
            }
            if (requestDTO.getCreatedAtFrom() != null) {
                builder.append(" and u.created_at >= :createdAtFrom ");
                params.put("createdAtFrom", requestDTO.getCreatedAtFrom());
            }
            if (requestDTO.getCreatedAtTo() != null) {
                builder.append(" and u.created_at < :createdAtTo ");
                params.put("createdAtTo", requestDTO.getCreatedAtTo());
            }
            if (!isCount) {
                builder.append(" order by ");
                builder.append(CommonUtils.joinWhenNoNull(" u.", requestDTO.getOrderBy(), " ", requestDTO.getOrderDirection(), ", "));
                builder.append(" u.id asc ");
            }
            if (!isAll) {
                builder.append(" limit :limit offset :offset ");
                params.put("limit", result.getPageSize());
                params.put("offset", ((result.getPageNumber() - 1) * result.getPageSize()));
            }
        }
        if (!isCount) {
            builder.append(" ) as filter on (filter.id = u.id) ");
            builder.append(" left join user_role ur on (ur.user_id = u.id) ");
            builder.append(" left join role r on (r.id = ur.role_id) ");
            builder.append(" left join role_permission rp on (rp.role_id = ur.role_id) ");
            builder.append(" left join permission p on (p.id = rp.permission_id) ");
            builder.append(" group by u.id ");
            builder.append(" order by ");
            builder.append(CommonUtils.joinWhenNoNull(" u.", requestDTO.getOrderBy(), " ", requestDTO.getOrderDirection(), ", "));
            builder.append(" u.id asc ");
        }
        if (isCount) {
            Query query = entityManager.createNativeQuery(builder.toString());
            sqlHelper.setParams(query, params);
            long count = ConversionUtils.safeToLong(query.getSingleResult());
            result.setTotalItems(count);
            result.setTotalPages(sqlHelper.toPages(count, result.getPageSize()));
        } else {
            Query query = entityManager.createNativeQuery(builder.toString(), ResultSetName.USER_SEARCH);
            sqlHelper.setParams(query, params);
            List<UserDTO> items = query.getResultList();
            result.setItems(items);
        }
        return result;
    }

    @Override
    public PaginationData<UserDTO> search(SearchUserRequestDTO requestDTO, boolean isCount) {
        PaginationData<UserDTO> result = executeSearch(requestDTO, true, true);
        if (!isCount && result.getTotalItems() > 0) {
            result.setItems(executeSearch(requestDTO, false, false).getItems());
        }
        return result;
    }

    @Override
    public UserDTO updateProfile(UserDTO requestDTO) {
        if (requestDTO.getId() == null) {
            throw new CustomException(HttpStatus.NOT_FOUND);
        }
        userValidator.validateUpdate(requestDTO);
        User user = userRepository.findById(requestDTO.getId()).orElse(null);
        if (user == null) {
            throw new CustomException(HttpStatus.NOT_FOUND);
        }
        if (StringUtils.isNotBlank(requestDTO.getUsername())) {
            if (!user.getUsername().equals(requestDTO.getUsername()) && userRepository.existsByUsername(requestDTO.getUsername())) {
                throw new CustomException(HttpStatus.CONFLICT);
            }
            user.setUsername(requestDTO.getUsername());
        }
        if (StringUtils.isNotBlank(requestDTO.getEmail())) {
            if (!user.getEmail().equals(requestDTO.getEmail()) && userRepository.existsByEmail(requestDTO.getEmail())) {
                throw new CustomException(HttpStatus.CONFLICT);
            }
            user.setEmail(requestDTO.getEmail());
        }
        if (StringUtils.isNotBlank(requestDTO.getName())) {
            user.setName(requestDTO.getName());
        }
        boolean isAdmin = authHelper.hasAnyPermission(PermissionCode.SYSTEM_ADMIN, PermissionCode.UPDATE_USER);
        if (isAdmin) {
            if (StringUtils.isNotEmpty(requestDTO.getStatus())) {
                user.setStatus(requestDTO.getStatus());
            }
            if (requestDTO.getRoleIds() != null) {
                roleService.setUserRoles(user.getId(), requestDTO.getRoleIds());
            }
        }
        return userMapper.toDTO(userRepository.save(user));
    }

    @Override
    public UserDTO findOneById(UUID userId) {
        if (userId == null) {
            return null;
        }
        List<UserDTO> result = executeSearch(SearchUserRequestDTO.builder().id(userId).build(), false, false).getItems();
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        return result.getFirst();
    }

    @Override
    public UserDTO findOneByIdOrThrow(UUID userId) {
        UserDTO result = findOneById(userId);
        if (result == null) {
            throw new CustomException(HttpStatus.NOT_FOUND);
        }
        return result;
    }

    @Override
    public UserDTO findOneByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            return null;
        }
        List<UserDTO> result = executeSearch(SearchUserRequestDTO.builder().username(username).build(), false, false).getItems();
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        return result.getFirst();
    }

    @Override
    public UserDTO findOneByEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return null;
        }
        List<UserDTO> result = executeSearch(SearchUserRequestDTO.builder().email(email).build(), false, false).getItems();
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        return result.getFirst();
    }

    @Override
    public void triggerExport(SearchUserRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        Instant cacheTime = Instant.now().truncatedTo(ChronoUnit.HOURS);
        requestDTO.setPageNumber(1L);
        requestDTO.setPageSize(MAX_ITEMS);
        requestDTO.setCreatedAtTo(cacheTime);
        requestDTO.setCacheTime(cacheTime);
        requestDTO.setCacheType(BackgroundTaskType.EXPORT_USER);
        String cacheKey = commonHelper.createCacheKey(requestDTO);
        BackgroundTaskDTO taskDTO = backgroundTaskService.init(cacheKey, BackgroundTaskType.EXPORT_USER);
        boolean succeeded = backgroundTaskService.completeByCacheKeyIfExist(cacheKey, BackgroundTaskType.EXPORT_USER, taskDTO.getId());
        if (!succeeded) {
            exportUserEventPublisher.publish(taskDTO.getId(), requestDTO);
        }
    }

    @Override
    public void handleExportTask(UUID backgroundTaskId, SearchUserRequestDTO requestDTO) {
        try {
            String cacheKey = commonHelper.createCacheKey(requestDTO);
            boolean succeeded = backgroundTaskService.completeByCacheKeyIfExist(cacheKey, BackgroundTaskType.EXPORT_USER, backgroundTaskId);
            if (succeeded) {
                return;
            }
            backgroundTaskService.updateStatus(backgroundTaskId, BackgroundTaskStatus.PROCESSING);
            FileObjectDTO fileObjectDTO = export(requestDTO);
            if (fileObjectDTO == null) {
                throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            backgroundTaskService.updateStatus(backgroundTaskId, BackgroundTaskStatus.SUCCEEDED, fileObjectDTO.getId());
        } catch (Exception e) {
            log.error("handleExportTask ", e);
            backgroundTaskService.updateStatus(backgroundTaskId, BackgroundTaskStatus.FAILED);
        }
    }

    private FileObjectDTO export(SearchUserRequestDTO requestDTO) throws IOException {
        if (requestDTO == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        PaginationData<UserDTO> searchData = executeSearch(requestDTO, false, false);
        if (searchData.getItems() == null) {
            searchData.setItems(new ArrayList<>());
        }
        byte[] file = null;
        try (Workbook workbook = new SXSSFWorkbook()) {
            Sheet sheet = workbook.createSheet();
            List<Object> header = List.of("Username", "Email", "Name", "Is OTP Enabled", "Is Verified", "Status", "Roles", "Permissions");
            ExcelUtils.setCellValues(sheet, 0, header);
            int idx = 1;
            for (UserDTO item : searchData.getItems()) {
                List<Object> data = Arrays.asList(
                        item.getUsername(),
                        item.getEmail(),
                        item.getName(),
                        ConversionUtils.safeToBoolean(item.getIsOtpEnabled()),
                        ConversionUtils.safeToBoolean(item.getIsVerified()),
                        item.getStatus(),
                        String.join(",", item.getRoleCodes()),
                        String.join(",", item.getPermissionCodes()));
                ExcelUtils.setCellValues(sheet, idx, data);
                idx++;
            }
            file = ExcelUtils.toBytes(workbook);
        }
        if (file == null || file.length == 0) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String filename = FileUtils.toFilename("ExportUsers_".concat(ConversionUtils.safeToString(DateUtils.currentEpochMillis())), FileType.XLSX);
        return fileObjectService.getFileUrls(fileObjectService.uploadFile(file, "", filename));
    }
}
