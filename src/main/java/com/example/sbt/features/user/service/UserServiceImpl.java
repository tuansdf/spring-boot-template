package com.example.sbt.features.user.service;

import com.example.sbt.common.constant.FileType;
import com.example.sbt.common.constant.PermissionCode;
import com.example.sbt.common.dto.PaginationData;
import com.example.sbt.common.dto.RequestContext;
import com.example.sbt.common.dto.RequestContextHolder;
import com.example.sbt.common.util.*;
import com.example.sbt.features.backgroundtask.constant.BackgroundTaskType;
import com.example.sbt.features.backgroundtask.dto.BackgroundTaskDTO;
import com.example.sbt.features.backgroundtask.entity.BackgroundTask;
import com.example.sbt.features.backgroundtask.service.BackgroundTaskService;
import com.example.sbt.features.file.dto.FileObjectDTO;
import com.example.sbt.features.file.service.FileObjectService;
import com.example.sbt.features.role.service.RoleService;
import com.example.sbt.features.user.dto.SearchUserRequest;
import com.example.sbt.features.user.dto.UserDTO;
import com.example.sbt.features.user.entity.User;
import com.example.sbt.features.user.event.ExportUserEventPublisher;
import com.example.sbt.features.user.mapper.UserMapper;
import com.example.sbt.features.user.repository.UserRepository;
import com.example.sbt.infrastructure.exception.CustomException;
import com.example.sbt.infrastructure.persistence.SQLHelper;
import com.example.sbt.infrastructure.security.AuthHelper;
import com.example.sbt.infrastructure.web.helper.CommonHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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

    private PaginationData<UserDTO> executeSearch(SearchUserRequest requestDTO) {
        requestDTO.setOrderBy(CommonUtils.inListOrNull(requestDTO.getOrderBy(), List.of("username", "email")));
        requestDTO.setOrderDirection(CommonUtils.inListOrNull(requestDTO.getOrderDirection(), List.of("asc", "desc")));
        PaginationData<UserDTO> result = sqlHelper.initData(requestDTO.getPageNumber(), requestDTO.getPageSize());
        List<Object> params = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        if (requestDTO.isCount()) {
            builder.append(" select count(*) ");
        } else {
            builder.append(" select ");
            builder.append(" u.id, u.username, u.email, u.name, u.created_at, u.updated_at, ");
            if (requestDTO.isDetail()) {
                builder.append(" u.is_enabled, u.is_verified, u.is_otp_enabled ");
            } else {
                builder.append(" null as is_enabled, null as is_verified, u.is_otp_enabled ");
            }
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
        if (!requestDTO.isCount()) {
            builder.append(" order by ");
            builder.append(CommonUtils.joinWhenNoNull(" u.", requestDTO.getOrderBy(), " ", requestDTO.getOrderDirection(), ", "));
            builder.append(" u.id asc ");
        }
        if (!requestDTO.isAll()) {
            builder.append(" limit ? offset ? ");
            sqlHelper.setLimitOffset(params, result.getPageNumber(), result.getPageSize());
        }
        if (requestDTO.isCount()) {
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
                if (requestDTO.isDetail()) {
                    dto.setIsEnabled(ConversionUtils.toBoolean(x[6]));
                    dto.setIsVerified(ConversionUtils.toBoolean(x[7]));
                    dto.setIsOtpEnabled(ConversionUtils.toBoolean(x[8]));
                }
                return dto;
            }).collect(Collectors.toCollection(ArrayList::new));
            result.setItems(items);
        }
        return result;
    }

    private List<UserDTO> executeSearchList(SearchUserRequest requestDTO) {
        requestDTO.setAll(false);
        requestDTO.setCount(false);
        return ConversionUtils.safeToList(executeSearch(requestDTO).getItems());
    }

    private UserDTO executeSearchOne(SearchUserRequest requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        requestDTO.setPageNumber(1L);
        requestDTO.setPageSize(1L);
        return executeSearchList(requestDTO).getFirst();
    }

    @Override
    public PaginationData<UserDTO> search(SearchUserRequest requestDTO, boolean isCount) {
        if (requestDTO == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        requestDTO.setAll(true);
        requestDTO.setCount(true);
        PaginationData<UserDTO> result = executeSearch(requestDTO);
        if (!isCount && result.getTotalItems() > 0) {
            requestDTO.setAll(false);
            requestDTO.setCount(false);
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
            backgroundTaskService.updateStatusIfCurrent(backgroundTaskId, BackgroundTask.Status.PROCESSING, BackgroundTask.Status.ENQUEUED);
            FileObjectDTO fileObjectDTO = export(requestDTO, requestContext);
            if (fileObjectDTO == null) {
                throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            backgroundTaskService.updateStatusIfCurrent(backgroundTaskId, BackgroundTask.Status.SUCCEEDED, BackgroundTask.Status.PROCESSING, fileObjectDTO.getId());
        } catch (Exception e) {
            log.error("handleExportTask {}", e.toString());
            backgroundTaskService.updateStatusIfCurrent(backgroundTaskId, BackgroundTask.Status.FAILED, BackgroundTask.Status.PROCESSING);
        }
    }

    private FileObjectDTO export(SearchUserRequest requestDTO, RequestContext requestContext) {
        if (requestDTO == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        List<UserDTO> users = executeSearchList(requestDTO);
        List<Object> header = List.of("Username", "Email", "Name", "Is Enabled", "Is Verified", "Is OTP Enabled");
        byte[] file = ExcelUtils.writeDataToBytes(header, users, (user) -> Arrays.asList(
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                ConversionUtils.safeToBoolean(user.getIsEnabled()),
                ConversionUtils.safeToBoolean(user.getIsVerified()),
                ConversionUtils.safeToBoolean(user.getIsOtpEnabled())
        ));
        if (file == null || file.length == 0) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String filename = FileUtils.toFilename("ExportUsers_" + DateUtils.currentEpochMillis(), FileType.XLSX);
        return fileObjectService.setFileUrls(fileObjectService.uploadFile(file, "", filename, requestContext));
    }
}
