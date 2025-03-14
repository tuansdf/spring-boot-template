package com.example.demo.module.role;

import com.example.demo.common.constant.CommonStatus;
import com.example.demo.common.constant.ResultSetName;
import com.example.demo.common.dto.PaginationResponseData;
import com.example.demo.common.exception.CustomException;
import com.example.demo.common.mapper.CommonMapper;
import com.example.demo.common.util.ConversionUtils;
import com.example.demo.common.util.SQLHelper;
import com.example.demo.module.role.dto.RoleDTO;
import com.example.demo.module.role.dto.SearchRoleRequestDTO;
import com.example.demo.module.user.UserRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class RoleServiceImpl implements RoleService {

    private final CommonMapper commonMapper;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final EntityManager entityManager;
    private final RoleValidator roleValidator;

    @Override
    public RoleDTO save(RoleDTO requestDTO) {
        Role result = null;
        if (requestDTO.getId() != null) {
            Optional<Role> roleOptional = roleRepository.findById(requestDTO.getId());
            if (roleOptional.isPresent()) {
                roleValidator.validateUpdate(requestDTO);
                result = roleOptional.get();
            }
        }
        if (result == null) {
            roleValidator.validateCreate(requestDTO);
            String code = ConversionUtils.toCode(requestDTO.getCode());
            if (roleRepository.existsByCode(code)) {
                throw new CustomException(HttpStatus.CONFLICT);
            }
            result = new Role();
            result.setCode(code);
        }
        result.setName(requestDTO.getName());
        result.setDescription(requestDTO.getDescription());
        if (requestDTO.getStatus() == null) {
            requestDTO.setStatus(CommonStatus.ACTIVE);
        }
        result.setStatus(requestDTO.getStatus());
        return commonMapper.toDTO(roleRepository.save(result));
    }

    @Override
    public void addToUser(UUID userId, Set<UUID> roleIds) {
        userRoleRepository.deleteAllByUserId(userId);
        List<UserRole> userRoles = new ArrayList<>();
        for (UUID roleId : roleIds) {
            userRoles.add(new UserRole(userId, roleId));
        }
        userRoleRepository.saveAll(userRoles);
    }

    @Override
    public RoleDTO findOneById(UUID id) {
        Optional<Role> result = roleRepository.findById(id);
        return result.map(commonMapper::toDTO).orElse(null);
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
        Optional<Role> result = roleRepository.findTopByCode(code);
        return result.map(commonMapper::toDTO).orElse(null);
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
        Set<String> result = roleRepository.findAllCodesByUserId(userId);
        if (result == null) {
            return new HashSet<>();
        }
        return result;
    }

    @Override
    public List<RoleDTO> findAllByUserId(UUID userId) {
        List<Role> result = roleRepository.findAllByUserId(userId);
        if (result == null) {
            return new ArrayList<>();
        }
        return result.stream().map(commonMapper::toDTO).toList();
    }

    @Override
    public PaginationResponseData<RoleDTO> search(SearchRoleRequestDTO requestDTO, boolean isCount) {
        PaginationResponseData<RoleDTO> result = executeSearch(requestDTO, true);
        if (!isCount && result.getTotalItems() > 0) {
            result.setItems(executeSearch(requestDTO, false).getItems());
        }
        return result;
    }

    private PaginationResponseData<RoleDTO> executeSearch(SearchRoleRequestDTO requestDTO, boolean isCount) {
        PaginationResponseData<RoleDTO> result = SQLHelper.initResponse(requestDTO.getPageNumber(), requestDTO.getPageSize());
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
        if (requestDTO.getStatus() != null) {
            builder.append(" and r.status = :status ");
            params.put("status", requestDTO.getStatus());
        }
        if (requestDTO.getCreatedAtFrom() != null) {
            builder.append(" and r.created_at >= :createdAtFrom ");
            params.put("createdAtFrom", requestDTO.getCreatedAtFrom().truncatedTo(ChronoUnit.MICROS));
        }
        if (requestDTO.getCreatedAtTo() != null) {
            builder.append(" and r.created_at <= :createdAtTo ");
            params.put("createdAtTo", requestDTO.getCreatedAtTo().truncatedTo(ChronoUnit.MICROS));
        }
        if (!isCount) {
            builder.append(SQLHelper.toLimitOffset(result.getPageNumber(), result.getPageSize()));
        }
        if (isCount) {
            Query query = entityManager.createNativeQuery(builder.toString());
            SQLHelper.setParams(query, params);
            long count = ConversionUtils.safeToLong(query.getSingleResult());
            result.setTotalItems(count);
            result.setTotalPages(SQLHelper.getTotalPages(count, result.getPageSize()));
        } else {
            Query query = entityManager.createNativeQuery(builder.toString(), ResultSetName.ROLE_SEARCH);
            SQLHelper.setParams(query, params);
            List<RoleDTO> items = query.getResultList();
            result.setItems(items);
        }
        return result;
    }

}
