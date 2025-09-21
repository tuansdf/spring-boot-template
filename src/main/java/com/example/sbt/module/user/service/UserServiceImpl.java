package com.example.sbt.module.user.service;

import com.example.sbt.common.constant.FileType;
import com.example.sbt.common.constant.PermissionCode;
import com.example.sbt.common.dto.PaginationData;
import com.example.sbt.common.dto.RequestContext;
import com.example.sbt.common.dto.RequestContextHolder;
import com.example.sbt.common.util.*;
import com.example.sbt.infrastructure.exception.CustomException;
import com.example.sbt.infrastructure.helper.AuthHelper;
import com.example.sbt.infrastructure.helper.CommonHelper;
import com.example.sbt.infrastructure.helper.SQLHelper;
import com.example.sbt.module.backgroundtask.constant.BackgroundTaskStatus;
import com.example.sbt.module.backgroundtask.constant.BackgroundTaskType;
import com.example.sbt.module.backgroundtask.dto.BackgroundTaskDTO;
import com.example.sbt.module.backgroundtask.service.BackgroundTaskService;
import com.example.sbt.module.file.dto.FileObjectDTO;
import com.example.sbt.module.file.service.FileObjectService;
import com.example.sbt.module.role.service.RoleService;
import com.example.sbt.module.user.dto.SearchUserRequest;
import com.example.sbt.module.user.dto.UserDTO;
import com.example.sbt.module.user.entity.User;
import com.example.sbt.module.user.event.ExportUserEventPublisher;
import com.example.sbt.module.user.mapper.UserMapper;
import com.example.sbt.module.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class UserServiceImpl implements UserService {
    private static final long MAX_ITEMS = 1_000_000L;

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

    private PaginationData<UserDTO> executeSearch(SearchUserRequest requestDTO, boolean isCount, boolean isAll) {
        requestDTO.setOrderBy(CommonUtils.inListOrNull(requestDTO.getOrderBy(), List.of("username", "email")));
        requestDTO.setOrderDirection(CommonUtils.inListOrNull(requestDTO.getOrderDirection(), List.of("asc", "desc")));
        PaginationData<UserDTO> result = sqlHelper.initData(requestDTO.getPageNumber(), requestDTO.getPageSize());
        List<Object> params = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        if (!isCount) {
            builder.append(" select ");
            builder.append(" u.id, u.username, u.email, u.name, u.created_at, u.updated_at, ");
            if (Boolean.TRUE.equals(requestDTO.getIsDetail())) {
                builder.append(" u.is_enabled, u.is_verified, u.is_otp_enabled, ");
                builder.append(" string_agg(distinct(r.code), ',') as roles, ");
                builder.append(" string_agg(distinct(p.code), ',') as permissions ");
            } else {
                builder.append(" null as is_enabled, null as is_verified, u.is_otp_enabled, ");
                builder.append(" null as roles, null as permissions ");
            }
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
                builder.append(" and u.id = ? ");
                params.add(requestDTO.getId());
            }
            if (StringUtils.isNotBlank(requestDTO.getUsername())) {
                builder.append(" and u.username = ? ");
                params.add(requestDTO.getUsername());
            }
            if (StringUtils.isNotBlank(requestDTO.getEmail())) {
                builder.append(" and u.email = ? ");
                params.add(requestDTO.getEmail());
            }
            if (requestDTO.getIdFrom() != null) {
                builder.append(" and u.id > ? ");
                params.add(requestDTO.getIdFrom());
            }
            if (StringUtils.isNotBlank(requestDTO.getUsernameFrom())) {
                builder.append(" and u.username > ? ");
                params.add(requestDTO.getUsernameFrom());
            }
            if (StringUtils.isNotBlank(requestDTO.getEmailFrom())) {
                builder.append(" and u.email > ? ");
                params.add(requestDTO.getEmailFrom());
            }
            if (requestDTO.getIsEnabled() != null) {
                builder.append(" and u.is_enabled = ? ");
                params.add(requestDTO.getIsEnabled());
            }
            if (requestDTO.getIsVerified() != null) {
                builder.append(" and u.is_verified = ? ");
                params.add(requestDTO.getIsVerified());
            }
            if (requestDTO.getCreatedAtFrom() != null) {
                builder.append(" and u.created_at >= ? ");
                params.add(requestDTO.getCreatedAtFrom());
            }
            if (requestDTO.getCreatedAtTo() != null) {
                builder.append(" and u.created_at < ? ");
                params.add(requestDTO.getCreatedAtTo());
            }
            if (!isCount) {
                builder.append(" order by ");
                builder.append(CommonUtils.joinWhenNoNull(" u.", requestDTO.getOrderBy(), " ", requestDTO.getOrderDirection(), ", "));
                builder.append(" u.id asc ");
            }
            if (!isAll) {
                builder.append(" limit ? offset ? ");
                sqlHelper.setLimitOffset(params, result.getPageNumber(), result.getPageSize());
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
            Query query = entityManager.createNativeQuery(builder.toString());
            sqlHelper.setParams(query, params);
            List<Object[]> objects = query.getResultList();
            List<UserDTO> items = objects.stream().map(x -> {
                UserDTO dto = new UserDTO();
                dto.setId(ConversionUtils.toUUID(x[0]));
                dto.setUsername(ConversionUtils.toString(x[1]));
                dto.setEmail(ConversionUtils.toString(x[2]));
                dto.setName(ConversionUtils.toString(x[3]));
                dto.setCreatedAt(DateUtils.toInstant(x[4]));
                dto.setUpdatedAt(DateUtils.toInstant(x[5]));
                if (Boolean.TRUE.equals(requestDTO.getIsDetail())) {
                    dto.setIsEnabled(ConversionUtils.toBoolean(x[6]));
                    dto.setIsVerified(ConversionUtils.toBoolean(x[7]));
                    dto.setIsOtpEnabled(ConversionUtils.toBoolean(x[8]));
                    dto.setRoleCodes(ConversionUtils.toString(x[9]));
                    dto.setPermissionCodes(ConversionUtils.toString(x[10]));
                }
                return dto;
            }).collect(Collectors.toCollection(ArrayList::new));
            result.setItems(items);
        }
        return result;
    }

    private List<UserDTO> executeSearchList(SearchUserRequest requestDTO) {
        return ConversionUtils.safeToList(executeSearch(requestDTO, false, false).getItems());
    }

    private UserDTO executeSearchOne(SearchUserRequest requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        requestDTO.setPageNumber(1L);
        requestDTO.setPageSize(1L);
        return executeSearchList(requestDTO).getFirst();
    }

    @Override
    public PaginationData<UserDTO> search(SearchUserRequest requestDTO, boolean isCount) {
        PaginationData<UserDTO> result = executeSearch(requestDTO, true, true);
        if (!isCount && result.getTotalItems() > 0) {
            result.setItems(executeSearchList(requestDTO));
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
        if (requestDTO.getUsername() != null) {
            requestDTO.setUsername(ConversionUtils.safeTrim(requestDTO.getUsername()));
            if (!user.getUsername().equals(requestDTO.getUsername()) && userRepository.existsByUsername(requestDTO.getUsername())) {
                throw new CustomException(HttpStatus.CONFLICT);
            }
            user.setUsername(requestDTO.getUsername());
        }
        if (requestDTO.getEmail() != null) {
            requestDTO.setEmail(ConversionUtils.safeTrim(requestDTO.getEmail()));
            if (!user.getEmail().equals(requestDTO.getEmail()) && userRepository.existsByEmail(requestDTO.getEmail())) {
                throw new CustomException(HttpStatus.CONFLICT);
            }
            user.setEmail(requestDTO.getEmail());
        }
        if (requestDTO.getName() != null) {
            requestDTO.setName(ConversionUtils.safeTrim(requestDTO.getName()));
            user.setName(requestDTO.getName());
        }
        boolean isAdmin = authHelper.hasAnyPermission(PermissionCode.SYSTEM_ADMIN, PermissionCode.UPDATE_USER);
        if (isAdmin) {
            if (requestDTO.getIsEnabled() != null) {
                user.setIsEnabled(requestDTO.getIsEnabled());
            }
            if (requestDTO.getRoleIds() != null) {
                roleService.setUserRoles(user.getId(), requestDTO.getRoleIds());
            }
        }
        return userMapper.toDTO(userRepository.save(user));
    }

    @Override
    public UserDTO findOneById(UUID userId) {
        if (userId == null) return null;
        return executeSearchOne(SearchUserRequest.builder().id(userId).isDetail(true).build());
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
        if (StringUtils.isBlank(username)) return null;
        return executeSearchOne(SearchUserRequest.builder().username(username).isDetail(true).build());
    }

    @Override
    public UserDTO findOneByEmail(String email) {
        if (StringUtils.isBlank(email)) return null;
        return executeSearchOne(SearchUserRequest.builder().email(email).isDetail(true).build());
    }

    @Override
    public void triggerExport(SearchUserRequest requestDTO) {
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
        BackgroundTaskDTO taskDTO = backgroundTaskService.init(cacheKey, BackgroundTaskType.EXPORT_USER, RequestContextHolder.get());
        boolean succeeded = backgroundTaskService.completeByCacheKeyIfExist(cacheKey, BackgroundTaskType.EXPORT_USER, taskDTO.getId());
        if (!succeeded) {
            exportUserEventPublisher.publish(taskDTO.getId(), requestDTO);
        }
    }

    @Override
    public void handleExportTask(UUID backgroundTaskId, SearchUserRequest requestDTO, RequestContext requestContext) {
        try {
            String cacheKey = commonHelper.createCacheKey(requestDTO);
            boolean succeeded = backgroundTaskService.completeByCacheKeyIfExist(cacheKey, BackgroundTaskType.EXPORT_USER, backgroundTaskId);
            if (succeeded) {
                return;
            }
            backgroundTaskService.updateStatus(backgroundTaskId, BackgroundTaskStatus.PROCESSING);
            FileObjectDTO fileObjectDTO = export(requestDTO, requestContext);
            if (fileObjectDTO == null) {
                throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            backgroundTaskService.updateStatus(backgroundTaskId, BackgroundTaskStatus.SUCCEEDED, fileObjectDTO.getId());
        } catch (Exception e) {
            log.error("handleExportTask {}", e.toString());
            backgroundTaskService.updateStatus(backgroundTaskId, BackgroundTaskStatus.FAILED);
        }
    }

    private FileObjectDTO export(SearchUserRequest requestDTO, RequestContext requestContext) throws IOException {
        if (requestDTO == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        List<UserDTO> users = executeSearchList(requestDTO);
        byte[] file = null;
        try (Workbook workbook = new SXSSFWorkbook()) {
            Sheet sheet = workbook.createSheet();
            List<Object> header = List.of("Username", "Email", "Name", "Is Enabled", "Is Verified", "Is OTP Enabled", "Roles", "Permissions");
            ExcelUtils.setCellValues(sheet, 0, header);
            int idx = 1;
            for (UserDTO user : users) {
                List<Object> data = Arrays.asList(
                        user.getUsername(),
                        user.getEmail(),
                        user.getName(),
                        ConversionUtils.safeToBoolean(user.getIsEnabled()),
                        ConversionUtils.safeToBoolean(user.getIsVerified()),
                        ConversionUtils.safeToBoolean(user.getIsOtpEnabled()),
                        String.join(",", user.getRoleCodes()),
                        String.join(",", user.getPermissionCodes()));
                ExcelUtils.setCellValues(sheet, idx, data);
                idx++;
            }
            file = ExcelUtils.toBytes(workbook);
        }
        if (file == null || file.length == 0) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String filename = FileUtils.toFilename("ExportUsers_" + ConversionUtils.safeToString(DateUtils.currentEpochMillis()), FileType.XLSX);
        return fileObjectService.setFileUrls(fileObjectService.uploadFile(file, "", filename, requestContext));
    }
}
