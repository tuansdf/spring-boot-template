package org.tuanna.xcloneserver.modules.permission;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.tuanna.xcloneserver.dtos.PaginationResponseData;
import org.tuanna.xcloneserver.entities.Permission;
import org.tuanna.xcloneserver.exception.CustomException;
import org.tuanna.xcloneserver.mappers.CommonMapper;
import org.tuanna.xcloneserver.modules.permission.dtos.PermissionDTO;
import org.tuanna.xcloneserver.modules.permission.dtos.SearchPermissionRequestDTO;
import org.tuanna.xcloneserver.utils.ConversionUtils;
import org.tuanna.xcloneserver.utils.SQLUtils;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class PermissionServiceImpl implements PermissionService {

    private final CommonMapper commonMapper;
    private final PermissionRepository permissionRepository;
    private final EntityManager entityManager;

    @Override
    public PermissionDTO save(PermissionDTO requestDTO, UUID byUser) throws CustomException {
        Permission result = null;
        if (requestDTO.getId() != null) {
            Optional<Permission> permissionOptional = permissionRepository.findById(requestDTO.getId());
            if (permissionOptional.isPresent()) {
                requestDTO.validateUpdate();
                result = permissionOptional.get();
            }
        }
        if (result == null) {
            requestDTO.validateCreate();
            result = new Permission();
            result.setCode(requestDTO.getCode());
            result.setCreatedBy(byUser);
        }
        result.setName(requestDTO.getName());
        result.setStatus(requestDTO.getStatus());
        result.setUpdatedBy(byUser);
        return commonMapper.toDTO(permissionRepository.save(result));
    }

    @Override
    public PermissionDTO findOneById(Long id) {
        Optional<Permission> result = permissionRepository.findById(id);
        return result.map(commonMapper::toDTO).orElse(null);
    }

    @Override
    public List<String> findAllCodesByRoleId(Long roleId) {
        List<String> result = permissionRepository.findAllCodesByRoleId(roleId);
        if (CollectionUtils.isEmpty(result)) {
            return new ArrayList<>();
        }
        return result;
    }

    @Override
    public List<String> findAllCodesByUserId(UUID userId) {
        List<String> result = permissionRepository.findAllCodesByUserId(userId);
        if (CollectionUtils.isEmpty(result)) {
            return new ArrayList<>();
        }
        return result;
    }

    @Override
    public List<PermissionDTO> findAllByRoleId(Long roleId) {
        List<Permission> result = permissionRepository.findAllByRoleId(roleId);
        if (CollectionUtils.isEmpty(result)) {
            return new ArrayList<>();
        }
        return result.stream().map(commonMapper::toDTO).toList();
    }

    @Override
    public List<PermissionDTO> findAllByUserId(UUID userId) {
        List<Permission> result = permissionRepository.findAllByUserId(userId);
        if (CollectionUtils.isEmpty(result)) {
            return new ArrayList<>();
        }
        return result.stream().map(commonMapper::toDTO).toList();
    }

    @Override
    public PaginationResponseData<PermissionDTO> search(SearchPermissionRequestDTO requestDTO, boolean isCountOnly) {
        PaginationResponseData<PermissionDTO> result = executeSearch(requestDTO, true);
        if (!isCountOnly && result.getTotalItems() > 0) {
            result.setItems(executeSearch(requestDTO, false).getItems());
        }
        return result;
    }

    private PaginationResponseData<PermissionDTO> executeSearch(SearchPermissionRequestDTO requestDTO, boolean isCount) {
        PaginationResponseData<PermissionDTO> result = SQLUtils.getPaginationResponseData(requestDTO.getPageNumber(), requestDTO.getPageSize());
        Map<String, Object> params = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        if (isCount) {
            builder.append(" select count(*) ");
        } else {
            builder.append(" select p.* ");
        }
        builder.append(" from permission p ");
        builder.append(" where 1=1 ");
        if (!StringUtils.isEmpty(requestDTO.getCode())) {
            builder.append(" and p.code = :code ");
            params.put("code", requestDTO.getCode());
        }
        if (!StringUtils.isEmpty(requestDTO.getStatus())) {
            builder.append(" and p.status = :status ");
            params.put("status", requestDTO.getStatus());
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
            builder.append(SQLUtils.getPaginationString(result.getPageNumber(), result.getPageSize()));
        }
        if (isCount) {
            Query query = entityManager.createNativeQuery(builder.toString());
            SQLUtils.setParams(query, params);
            Long count = ConversionUtils.toLong(query.getSingleResult());
            result.setTotalItems(count);
        } else {
            Query query = entityManager.createNativeQuery(builder.toString(), Tuple.class);
            SQLUtils.setParams(query, params);
            List<Tuple> tuples = query.getResultList();
            result.setItems(PermissionDTO.fromTuples(tuples));
        }
        return result;
    }

}
