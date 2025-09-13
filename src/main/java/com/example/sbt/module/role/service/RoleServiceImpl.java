package com.example.sbt.module.role.service;

import com.example.sbt.common.dto.PaginationData;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.common.util.DateUtils;
import com.example.sbt.infrastructure.exception.CustomException;
import com.example.sbt.infrastructure.helper.SQLHelper;
import com.example.sbt.module.permission.service.PermissionService;
import com.example.sbt.module.role.dto.RoleDTO;
import com.example.sbt.module.role.dto.SearchRoleRequest;
import com.example.sbt.module.role.entity.Role;
import com.example.sbt.module.role.mapper.RoleMapper;
import com.example.sbt.module.role.repository.RoleRepository;
import com.example.sbt.module.user.entity.UserRole;
import com.example.sbt.module.user.repository.UserRoleRepository;
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
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class RoleServiceImpl implements RoleService {
    private final SQLHelper sqlHelper;
    private final RoleMapper roleMapper;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final EntityManager entityManager;
    private final RoleValidator roleValidator;
    private final PermissionService permissionService;

    @Override
    public RoleDTO save(RoleDTO requestDTO) {
        roleValidator.cleanRequest(requestDTO);
        roleValidator.validateUpdate(requestDTO);
        Role result = null;
        if (requestDTO.getId() != null) {
            result = roleRepository.findById(requestDTO.getId()).orElse(null);
        }
        if (result == null) {
            roleValidator.validateCreate(requestDTO);
            result = new Role();
            result.setCode(requestDTO.getCode());
        }
        result.setName(requestDTO.getName());
        result.setDescription(requestDTO.getDescription());
        result = roleRepository.save(result);
        permissionService.setRolePermissions(result.getId(), requestDTO.getPermissionIds());
        return roleMapper.toDTO(result);
    }

    @Override
    public void setUserRoles(UUID userId, List<UUID> roleIds) {
        if (userId == null) return;
        if (CollectionUtils.isEmpty(roleIds)) {
            userRoleRepository.deleteAllByUserId(userId);
            return;
        }
        Set<UUID> existingIds = new HashSet<>(roleRepository.findAllIdsByUserId(userId));
        Set<UUID> removeIds = new HashSet<>(existingIds);
        Set<UUID> newIds = new HashSet<>(roleIds);
        removeIds.removeAll(newIds);
        newIds.removeAll(existingIds);
        if (CollectionUtils.isNotEmpty(removeIds)) {
            userRoleRepository.deleteAllByUserIdAndRoleIdIn(userId, new ArrayList<>(removeIds));
        }
        if (CollectionUtils.isNotEmpty(newIds)) {
            userRoleRepository.saveAll(newIds.stream().map(roleId -> UserRole.builder().userId(userId).roleId(roleId).build()).toList());
        }
    }

    @Override
    public RoleDTO findOneById(UUID id) {
        if (id == null) return null;
        return roleRepository.findById(id).map(roleMapper::toDTO).orElse(null);
    }

    @Override
    public RoleDTO findOneByIdOrThrow(UUID id) {
        RoleDTO result = findOneById(id);
        if (result == null) {
            throw new CustomException(HttpStatus.NOT_FOUND);
        }
        return result;
    }

    @Override
    public RoleDTO findOneByCode(String code) {
        if (StringUtils.isBlank(code)) return null;
        return roleRepository.findTopByCode(code).map(roleMapper::toDTO).orElse(null);
    }

    @Override
    public RoleDTO findOneByCodeOrThrow(String code) {
        RoleDTO result = findOneByCode(code);
        if (result == null) {
            throw new CustomException(HttpStatus.NOT_FOUND);
        }
        return result;
    }

    @Override
    public List<String> findAllCodesByUserId(UUID userId) {
        if (userId == null) return new ArrayList<>();
        List<String> result = roleRepository.findAllCodesByUserId(userId);
        if (result == null) return new ArrayList<>();
        return result;
    }

    @Override
    public List<RoleDTO> findAllByUserId(UUID userId) {
        if (userId == null) return new ArrayList<>();
        List<Role> result = roleRepository.findAllByUserId(userId);
        if (result == null) return new ArrayList<>();
        return result.stream().map(roleMapper::toDTO).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public PaginationData<RoleDTO> search(SearchRoleRequest requestDTO, boolean isCount) {
        PaginationData<RoleDTO> result = executeSearch(requestDTO, true);
        if (!isCount && result.getTotalItems() > 0) {
            result.setItems(executeSearch(requestDTO, false).getItems());
        }
        return result;
    }

    private PaginationData<RoleDTO> executeSearch(SearchRoleRequest requestDTO, boolean isCount) {
        PaginationData<RoleDTO> result = sqlHelper.initData(requestDTO.getPageNumber(), requestDTO.getPageSize());
        List<Object> params = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        if (isCount) {
            builder.append(" select count(*) ");
        } else {
            builder.append(" select r.id, r.code, r.name, r.description, r.created_at, r.updated_at ");
        }
        builder.append(" from role r ");
        builder.append(" where 1=1 ");
        if (StringUtils.isNotBlank(requestDTO.getCode())) {
            builder.append(" and r.code = ? ");
            params.add(requestDTO.getCode().trim());
        }
        if (requestDTO.getCreatedAtFrom() != null) {
            builder.append(" and r.created_at >= ? ");
            params.add(requestDTO.getCreatedAtFrom());
        }
        if (requestDTO.getCreatedAtTo() != null) {
            builder.append(" and r.created_at < ? ");
            params.add(requestDTO.getCreatedAtTo());
        }
        if (!isCount) {
            builder.append(" order by r.code asc, r.id asc ");
            builder.append(" limit ? offset ? ");
            sqlHelper.setLimitOffset(params, result.getPageNumber(), result.getPageSize());
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
            List<RoleDTO> items = objects.stream().map(x -> {
                RoleDTO dto = new RoleDTO();
                dto.setId(ConversionUtils.toUUID(x[0]));
                dto.setCode(ConversionUtils.toString(x[1]));
                dto.setName(ConversionUtils.toString(x[2]));
                dto.setDescription(ConversionUtils.toString(x[3]));
                dto.setCreatedAt(DateUtils.toInstant(x[4]));
                dto.setUpdatedAt(DateUtils.toInstant(x[5]));
                return dto;
            }).collect(Collectors.toCollection(ArrayList::new));
            result.setItems(items);
        }
        return result;
    }
}
