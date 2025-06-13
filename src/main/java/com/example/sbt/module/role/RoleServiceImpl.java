package com.example.sbt.module.role;

import com.example.sbt.common.constant.ResultSetName;
import com.example.sbt.common.dto.PaginationData;
import com.example.sbt.common.exception.CustomException;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.common.util.SQLHelper;
import com.example.sbt.module.permission.PermissionService;
import com.example.sbt.module.role.dto.RoleDTO;
import com.example.sbt.module.role.dto.SearchRoleRequestDTO;
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

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class RoleServiceImpl implements RoleService {

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
    public void setUserRoles(UUID userId, Set<UUID> roleIds) {
        if (userId == null) return;
        if (CollectionUtils.isEmpty(roleIds)) {
            userRoleRepository.deleteAllByUserId(userId);
            return;
        }
        Set<UUID> existingIds = roleRepository.findAllIdsByUserId(userId);
        Set<UUID> removeIds = new HashSet<>(existingIds);
        removeIds.removeAll(roleIds);
        Set<UUID> newIds = new HashSet<>(roleIds);
        newIds.removeAll(existingIds);
        if (CollectionUtils.isNotEmpty(removeIds)) {
            userRoleRepository.deleteAllByUserIdAndRoleIdIn(userId, removeIds);
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
    public Set<String> findAllCodesByUserId(UUID userId) {
        if (userId == null) return new HashSet<>();
        Set<String> result = roleRepository.findAllCodesByUserId(userId);
        if (result == null) return new HashSet<>();
        return result;
    }

    @Override
    public List<RoleDTO> findAllByUserId(UUID userId) {
        if (userId == null) return new ArrayList<>();
        List<Role> result = roleRepository.findAllByUserId(userId);
        if (result == null) return new ArrayList<>();
        return result.stream().map(roleMapper::toDTO).toList();
    }

    @Override
    public PaginationData<RoleDTO> search(SearchRoleRequestDTO requestDTO, boolean isCount) {
        PaginationData<RoleDTO> result = executeSearch(requestDTO, true);
        if (!isCount && result.getTotalItems() > 0) {
            result.setItems(executeSearch(requestDTO, false).getItems());
        }
        return result;
    }

    private PaginationData<RoleDTO> executeSearch(SearchRoleRequestDTO requestDTO, boolean isCount) {
        PaginationData<RoleDTO> result = SQLHelper.initData(requestDTO.getPageNumber(), requestDTO.getPageSize());
        Map<String, Object> params = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        if (isCount) {
            builder.append(" select count(*) ");
        } else {
            builder.append(" select r.* ");
        }
        builder.append(" from role r ");
        builder.append(" where 1=1 ");
        if (StringUtils.isNotBlank(requestDTO.getCode())) {
            builder.append(" and r.code = :code ");
            params.put("code", requestDTO.getCode());
        }
        if (requestDTO.getCreatedAtFrom() != null) {
            builder.append(" and r.created_at >= :createdAtFrom ");
            params.put("createdAtFrom", requestDTO.getCreatedAtFrom().truncatedTo(SQLHelper.MIN_TIME_PRECISION));
        }
        if (requestDTO.getCreatedAtTo() != null) {
            builder.append(" and r.created_at <= :createdAtTo ");
            params.put("createdAtTo", requestDTO.getCreatedAtTo().truncatedTo(SQLHelper.MIN_TIME_PRECISION));
        }
        if (!isCount) {
            builder.append(" order by r.code asc, r.id asc ");
            builder.append(SQLHelper.toLimitOffset(result.getPageNumber(), result.getPageSize()));
        }
        if (isCount) {
            Query query = entityManager.createNativeQuery(builder.toString());
            SQLHelper.setParams(query, params);
            long count = ConversionUtils.safeToLong(query.getSingleResult());
            result.setTotalItems(count);
            result.setTotalPages(SQLHelper.toPages(count, result.getPageSize()));
        } else {
            Query query = entityManager.createNativeQuery(builder.toString(), ResultSetName.ROLE_SEARCH);
            SQLHelper.setParams(query, params);
            List<RoleDTO> items = query.getResultList();
            result.setItems(items);
        }
        return result;
    }

}
