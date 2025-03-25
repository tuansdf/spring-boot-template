package com.example.sbt.module.permission;

import com.example.sbt.common.constant.CommonStatus;
import com.example.sbt.common.constant.ResultSetName;
import com.example.sbt.common.dto.PaginationData;
import com.example.sbt.common.exception.CustomException;
import com.example.sbt.common.mapper.CommonMapper;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.common.util.SQLHelper;
import com.example.sbt.module.permission.dto.PermissionDTO;
import com.example.sbt.module.permission.dto.SearchPermissionRequestDTO;
import com.example.sbt.module.role.entity.RolePermission;
import com.example.sbt.module.role.repository.RolePermissionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class PermissionServiceImpl implements PermissionService {

    private final CommonMapper commonMapper;
    private final PermissionRepository permissionRepository;
    private final EntityManager entityManager;
    private final PermissionValidator permissionValidator;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public PermissionDTO save(PermissionDTO requestDTO) {
        Permission result = null;
        if (requestDTO.getId() != null) {
            Optional<Permission> permissionOptional = permissionRepository.findById(requestDTO.getId());
            if (permissionOptional.isPresent()) {
                permissionValidator.validateUpdate(requestDTO);
                result = permissionOptional.get();
            }
        }
        if (result == null) {
            permissionValidator.validateCreate(requestDTO);
            String code = ConversionUtils.safeToString(requestDTO.getCode()).trim().toUpperCase();
            if (permissionRepository.existsByCode(code)) {
                throw new CustomException(HttpStatus.CONFLICT);
            }
            result = new Permission();
            result.setCode(code);
        }
        result.setName(requestDTO.getName());
        if (requestDTO.getStatus() == null) {
            requestDTO.setStatus(CommonStatus.ACTIVE);
        }
        result.setStatus(requestDTO.getStatus());
        return commonMapper.toDTO(permissionRepository.save(result));
    }

    @Override
    public void setRolePermissions(UUID roleId, Set<UUID> permissionIds) {
        rolePermissionRepository.deleteAllByRoleId(roleId);
        if (CollectionUtils.isEmpty(permissionIds)) return;
        List<RolePermission> rolePermissions = new ArrayList<>();
        for (UUID permissionId : permissionIds) {
            rolePermissions.add(new RolePermission(roleId, permissionId));
        }
        rolePermissionRepository.saveAll(rolePermissions);
    }

    @Override
    public PermissionDTO findOneById(UUID id) {
        Optional<Permission> result = permissionRepository.findById(id);
        return result.map(commonMapper::toDTO).orElse(null);
    }

    @Override
    public PermissionDTO findOneByIdOrThrow(UUID id) {
        PermissionDTO result = findOneById(id);
        if (result == null) {
            throw new CustomException(HttpStatus.NOT_FOUND);
        }
        return result;
    }

    @Override
    public PermissionDTO findOneByCode(String code) {
        Optional<Permission> result = permissionRepository.findTopByCode(code);
        return result.map(commonMapper::toDTO).orElse(null);
    }

    @Override
    public PermissionDTO findOneByCodeOrThrow(String code) {
        PermissionDTO result = findOneByCode(code);
        if (result == null) {
            throw new CustomException(HttpStatus.NOT_FOUND);
        }
        return result;
    }

    @Override
    public Set<UUID> findAllIdsByRoleId(UUID roleId) {
        Set<UUID> result = permissionRepository.findAllIdsByRoleId(roleId);
        if (result == null) {
            return new HashSet<>();
        }
        return result;
    }

    @Override
    public Set<String> findAllCodesByRoleId(UUID roleId) {
        Set<String> result = permissionRepository.findAllCodesByRoleId(roleId);
        if (result == null) {
            return new HashSet<>();
        }
        return result;
    }

    @Override
    public Set<String> findAllCodesByUserId(UUID userId) {
        Set<String> result = permissionRepository.findAllCodesByUserId(userId);
        if (result == null) {
            return new HashSet<>();
        }
        return result;
    }

    @Override
    public List<PermissionDTO> findAllByRoleId(UUID roleId) {
        List<Permission> result = permissionRepository.findAllByRoleId(roleId);
        if (result == null) {
            return new ArrayList<>();
        }
        return result.stream().map(commonMapper::toDTO).toList();
    }

    @Override
    public List<PermissionDTO> findAllByUserId(UUID userId) {
        List<Permission> result = permissionRepository.findAllByUserId(userId);
        if (result == null) {
            return new ArrayList<>();
        }
        return result.stream().map(commonMapper::toDTO).toList();
    }

    @Override
    public PaginationData<PermissionDTO> search(SearchPermissionRequestDTO requestDTO, boolean isCount) {
        PaginationData<PermissionDTO> result = executeSearch(requestDTO, true);
        if (!isCount && result.getTotalItems() > 0) {
            result.setItems(executeSearch(requestDTO, false).getItems());
        }
        return result;
    }

    private PaginationData<PermissionDTO> executeSearch(SearchPermissionRequestDTO requestDTO, boolean isCount) {
        PaginationData<PermissionDTO> result = SQLHelper.initData(requestDTO.getPageNumber(), requestDTO.getPageSize());
        Map<String, Object> params = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        if (isCount) {
            builder.append(" select count(*) ");
        } else {
            builder.append(" select p.* ");
        }
        builder.append(" from permission p ");
        builder.append(" where 1=1 ");
        if (StringUtils.isNotEmpty(requestDTO.getCode())) {
            builder.append(" and p.code = :code ");
            params.put("code", requestDTO.getCode());
        }
        if (requestDTO.getStatus() != null) {
            builder.append(" and p.status = :status ");
            params.put("status", requestDTO.getStatus());
        }
        if (requestDTO.getCreatedAtFrom() != null) {
            builder.append(" and p.created_at >= :createdAtFrom ");
            params.put("createdAtFrom", requestDTO.getCreatedAtFrom().truncatedTo(SQLHelper.MIN_TIME_PRECISION));
        }
        if (requestDTO.getCreatedAtTo() != null) {
            builder.append(" and p.created_at <= :createdAtTo ");
            params.put("createdAtTo", requestDTO.getCreatedAtTo().truncatedTo(SQLHelper.MIN_TIME_PRECISION));
        }
        if (!isCount) {
            builder.append(SQLHelper.toLimitOffset(result.getPageNumber(), result.getPageSize()));
        }
        if (isCount) {
            Query query = entityManager.createNativeQuery(builder.toString());
            SQLHelper.setParams(query, params);
            long count = ConversionUtils.safeToLong(query.getSingleResult());
            result.setTotalItems(count);
            result.setTotalPages(SQLHelper.toPages(count, result.getPageSize()));
        } else {
            Query query = entityManager.createNativeQuery(builder.toString(), ResultSetName.PERMISSION_SEARCH);
            SQLHelper.setParams(query, params);
            List<PermissionDTO> items = query.getResultList();
            result.setItems(items);
        }
        return result;
    }

}
