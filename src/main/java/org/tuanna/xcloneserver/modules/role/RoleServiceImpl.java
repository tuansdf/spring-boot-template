package org.tuanna.xcloneserver.modules.role;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.tuanna.xcloneserver.constants.Constants;
import org.tuanna.xcloneserver.dtos.PaginationResponseData;
import org.tuanna.xcloneserver.entities.Role;
import org.tuanna.xcloneserver.exception.CustomException;
import org.tuanna.xcloneserver.mappers.CommonMapper;
import org.tuanna.xcloneserver.modules.role.dtos.RoleDTO;
import org.tuanna.xcloneserver.modules.role.dtos.SearchRoleRequestDTO;
import org.tuanna.xcloneserver.utils.ConversionUtils;
import org.tuanna.xcloneserver.utils.SQLUtils;
import org.tuanna.xcloneserver.utils.ValidationUtils;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    private final CommonMapper commonMapper;
    private final RoleRepository roleRepository;
    private final EntityManager entityManager;

    @Override
    public RoleDTO save(RoleDTO roleDTO, UUID byUser) throws CustomException {
        Role role = null;
        if (roleDTO.getId() != null) {
            Optional<Role> roleOptional = roleRepository.findById(roleDTO.getId());
            role = roleOptional.orElse(null);
        }
        if (role == null) {
            ValidationUtils.startsWith(roleDTO.getCode(), Constants.ROLE_STARTS_WITH, "Missing code");

            role = new Role();
            role.setCode(roleDTO.getCode());
            role.setCreatedBy(byUser);
        }
        role.setName(roleDTO.getName());
        role.setDescription(roleDTO.getDescription());
        role.setStatus(roleDTO.getStatus());
        role.setUpdatedBy(byUser);
        return commonMapper.toDTO(roleRepository.save(role));
    }

    @Override
    public RoleDTO findOneById(Long id) {
        Optional<Role> result = roleRepository.findById(id);
        return result.map(commonMapper::toDTO).orElse(null);
    }

    @Override
    public List<String> findAllCodesByUserId(UUID userId) {
        List<String> result = roleRepository.findAllCodesByUserId(userId);
        if (CollectionUtils.isEmpty(result)) {
            return new ArrayList<>();
        }
        return result;
    }

    @Override
    public List<RoleDTO> findAllByUserId(UUID userId) {
        List<Role> result = roleRepository.findAllByUserId(userId);
        if (CollectionUtils.isEmpty(result)) {
            return new ArrayList<>();
        }
        return result.stream().map(commonMapper::toDTO).toList();
    }

    @Override
    public PaginationResponseData<RoleDTO> search(SearchRoleRequestDTO requestDTO, boolean isCountOnly) {
        PaginationResponseData<RoleDTO> result = executeSearch(requestDTO, true);
        if (!isCountOnly && result.getTotalItems() > 0) {
            result.setItems(executeSearch(requestDTO, false).getItems());
        }
        return result;
    }

    private PaginationResponseData<RoleDTO> executeSearch(SearchRoleRequestDTO requestDTO, boolean isCount) {
        PaginationResponseData<RoleDTO> result = SQLUtils.getPaginationResponseData(requestDTO.getPageNumber(), requestDTO.getPageSize());
        Map<String, Object> params = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        if (isCount) {
            builder.append(" select count(*) ");
        } else {
            builder.append(" select r.* ");
        }
        builder.append(" from role r ");
        builder.append(" where 1=1 ");
        if (!StringUtils.isEmpty(requestDTO.getCode())) {
            builder.append(" and r.code = :code ");
            params.put("code", requestDTO.getCode());
        }
        if (!StringUtils.isEmpty(requestDTO.getStatus())) {
            builder.append(" and r.status = :status ");
            params.put("status", requestDTO.getStatus());
        }
        if (requestDTO.getCreatedAtFrom() != null) {
            builder.append(" and r.created_at >= :createdAtFrom ");
            params.put("createdAtFrom", requestDTO.getCreatedAtFrom());
        }
        if (requestDTO.getCreatedAtTo() != null) {
            builder.append(" and r.created_at <= :createdAtTo ");
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
            result.setItems(RoleDTO.fromTuples(tuples));
        }
        return result;
    }

}
