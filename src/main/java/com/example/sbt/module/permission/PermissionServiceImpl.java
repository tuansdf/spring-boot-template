package com.example.sbt.module.permission;

import com.example.sbt.core.exception.CustomException;
import com.example.sbt.core.mapper.CommonMapper;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.core.util.SQLHelper;
import com.example.sbt.core.constant.ResultSetName;
import com.example.sbt.core.dto.PaginationData;
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
        permissionValidator.cleanRequest(requestDTO);
        permissionValidator.validateUpdate(requestDTO);
        Permission result = null;
        if (requestDTO.getId() != null) {
            result = permissionRepository.findById(requestDTO.getId()).orElse(null);
        }
        if (result == null) {
            permissionValidator.validateCreate(requestDTO);
            result = new Permission();
            result.setCode(requestDTO.getCode());
        }
        result.setName(requestDTO.getName());
        return commonMapper.toDTO(permissionRepository.save(result));
    }

    @Override
    public void setRolePermissions(UUID roleId, List<UUID> permissionIds) {
        if (roleId == null) return;
        if (CollectionUtils.isEmpty(permissionIds)) {
            rolePermissionRepository.deleteAllByRoleId(roleId);
            return;
        }
        Set<UUID> existingIds = new HashSet<>(permissionRepository.findAllIdsByRoleId(roleId));
        Set<UUID> removeIds = new HashSet<>(existingIds);
        Set<UUID> newIds = new HashSet<>(permissionIds);
        removeIds.removeAll(newIds);
        newIds.removeAll(existingIds);
        if (CollectionUtils.isNotEmpty(removeIds)) {
            rolePermissionRepository.deleteAllByRoleIdAndPermissionIdIn(roleId, new ArrayList<>(removeIds));
        }
        if (CollectionUtils.isNotEmpty(newIds)) {
            rolePermissionRepository.saveAll(newIds.stream().map(permissionId -> RolePermission.builder().roleId(roleId).permissionId(permissionId).build()).toList());
        }
    }

    @Override
    public PermissionDTO findOneById(UUID id) {
        if (id == null) return null;
        return permissionRepository.findById(id).map(commonMapper::toDTO).orElse(null);
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
        if (StringUtils.isBlank(code)) return null;
        return permissionRepository.findTopByCode(code).map(commonMapper::toDTO).orElse(null);
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
    public List<UUID> findAllIdsByRoleId(UUID roleId) {
        if (roleId == null) return new ArrayList<>();
        List<UUID> result = permissionRepository.findAllIdsByRoleId(roleId);
        if (result == null) return new ArrayList<>();
        return result;
    }

    @Override
    public List<String> findAllCodesByRoleId(UUID roleId) {
        if (roleId == null) return new ArrayList<>();
        List<String> result = permissionRepository.findAllCodesByRoleId(roleId);
        if (result == null) return new ArrayList<>();
        return result;
    }

    @Override
    public List<String> findAllCodesByUserId(UUID userId) {
        if (userId == null) return new ArrayList<>();
        List<String> result = permissionRepository.findAllCodesByUserId(userId);
        if (result == null) return new ArrayList<>();
        return result;
    }

    @Override
    public List<PermissionDTO> findAllByRoleId(UUID roleId) {
        if (roleId == null) return new ArrayList<>();
        List<Permission> result = permissionRepository.findAllByRoleId(roleId);
        if (result == null) return new ArrayList<>();
        return result.stream().map(commonMapper::toDTO).toList();
    }

    @Override
    public List<PermissionDTO> findAllByUserId(UUID userId) {
        if (userId == null) return new ArrayList<>();
        List<Permission> result = permissionRepository.findAllByUserId(userId);
        if (result == null) return new ArrayList<>();
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
        if (StringUtils.isNotBlank(requestDTO.getCode())) {
            builder.append(" and p.code = :code ");
            params.put("code", requestDTO.getCode().trim());
        }
        if (requestDTO.getCreatedAtFrom() != null) {
            builder.append(" and p.created_at >= :createdAtFrom ");
            params.put("createdAtFrom", requestDTO.getCreatedAtFrom());
        }
        if (requestDTO.getCreatedAtTo() != null) {
            builder.append(" and p.created_at <= :createdAtTo ");
            params.put("createdAtTo", requestDTO.getCreatedAtTo());
        }
        if (!isCount) {
            builder.append(" order by p.code asc, p.id asc ");
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
