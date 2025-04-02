package com.example.sbt.module.user;

import com.example.sbt.common.constant.PermissionCode;
import com.example.sbt.common.constant.ResultSetName;
import com.example.sbt.common.dto.PaginationData;
import com.example.sbt.common.exception.CustomException;
import com.example.sbt.common.util.AuthHelper;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.common.util.SQLHelper;
import com.example.sbt.module.role.RoleService;
import com.example.sbt.module.user.dto.SearchUserRequestDTO;
import com.example.sbt.module.user.dto.UserDTO;
import com.example.sbt.module.user.entity.User;
import com.example.sbt.module.user.mapper.UserMapper;
import com.example.sbt.module.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final RoleService roleService;

    @Override
    public UserDTO updateProfile(UserDTO requestDTO) {
        boolean isAdmin = AuthHelper.hasAnyPermission(PermissionCode.SYSTEM_ADMIN, PermissionCode.UPDATE_USER);
        if (requestDTO.getId() == null) {
            throw new CustomException(HttpStatus.NOT_FOUND);
        }
        userValidator.validateUpdate(requestDTO);
        Optional<User> userOptional = userRepository.findById(requestDTO.getId());
        if (userOptional.isEmpty()) {
            throw new CustomException(HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
        if (StringUtils.isNotEmpty(requestDTO.getUsername())) {
            if (!ConversionUtils.safeToString(user.getUsername()).equals(requestDTO.getUsername()) && existsByUsername(requestDTO.getUsername())) {
                throw new CustomException(HttpStatus.CONFLICT);
            }
            user.setUsername(requestDTO.getUsername());
        }
        if (StringUtils.isNotEmpty(requestDTO.getEmail())) {
            if (!ConversionUtils.safeToString(user.getEmail()).equals(requestDTO.getEmail()) && existsByEmail(requestDTO.getEmail())) {
                throw new CustomException(HttpStatus.CONFLICT);
            }
            user.setEmail(requestDTO.getEmail());
        }
        if (StringUtils.isNotEmpty(requestDTO.getName())) {
            user.setName(requestDTO.getName());
        }
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
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsernameOrEmail(String username, String email) {
        return userRepository.existsByUsernameOrEmail(username, email);
    }

    @Override
    public UserDTO findOneById(UUID userId) {
        Optional<User> result = userRepository.findById(userId);
        return result.map(userMapper::toDTO).orElse(null);
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
        Optional<User> result = userRepository.findTopByUsername(username);
        return result.map(userMapper::toDTO).orElse(null);
    }

    @Override
    public UserDTO findOneByUsernameOrThrow(String username) {
        UserDTO result = findOneByUsername(username);
        if (result == null) {
            throw new CustomException(HttpStatus.NOT_FOUND);
        }
        return result;
    }

    @Override
    public UserDTO findOneByEmail(String email) {
        Optional<User> result = userRepository.findTopByEmail(email);
        return result.map(userMapper::toDTO).orElse(null);
    }

    @Override
    public UserDTO findOneByEmailOrThrow(String email) {
        UserDTO result = findOneByEmail(email);
        if (result == null) {
            throw new CustomException(HttpStatus.NOT_FOUND);
        }
        return result;
    }

    @Override
    public PaginationData<UserDTO> search(SearchUserRequestDTO requestDTO, boolean isCount) {
        PaginationData<UserDTO> result = executeSearch(requestDTO, true);
        if (!isCount && result.getTotalItems() > 0) {
            result.setItems(executeSearch(requestDTO, false).getItems());
        }
        return result;
    }

    private PaginationData<UserDTO> executeSearch(SearchUserRequestDTO requestDTO, boolean isCount) {
        PaginationData<UserDTO> result = SQLHelper.initData(requestDTO.getPageNumber(), requestDTO.getPageSize());
        Map<String, Object> params = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        if (isCount) {
            builder.append(" select count(*) ");
        } else {
            builder.append(" select u.*, ");
            builder.append(" string_agg(distinct(r.code), ',') as roles, ");
            builder.append(" string_agg(distinct(p.code), ',') as permissions ");
        }
        builder.append(" from _user u ");
        builder.append(" inner join ( ");
        {
            builder.append(" select u.id ");
            builder.append(" from _user u ");
            builder.append(" where 1=1 ");
            if (StringUtils.isNotEmpty(requestDTO.getUsername())) {
                builder.append(" and u.username like :username ");
                params.put("username", SQLHelper.escapeLikePattern(requestDTO.getUsername()).concat("%"));
            }
            if (StringUtils.isNotEmpty(requestDTO.getEmail())) {
                builder.append(" and u.email like :email ");
                params.put("email", SQLHelper.escapeLikePattern(requestDTO.getEmail()).concat("%"));
            }
            if (StringUtils.isNotEmpty(requestDTO.getStatus())) {
                builder.append(" and u.status = :status ");
                params.put("status", requestDTO.getStatus());
            }
            if (requestDTO.getCreatedAtFrom() != null) {
                builder.append(" and u.created_at >= :createdAtFrom ");
                params.put("createdAtFrom", requestDTO.getCreatedAtFrom().truncatedTo(SQLHelper.MIN_TIME_PRECISION));
            }
            if (requestDTO.getCreatedAtTo() != null) {
                builder.append(" and u.created_at <= :createdAtTo ");
                params.put("createdAtTo", requestDTO.getCreatedAtTo().truncatedTo(SQLHelper.MIN_TIME_PRECISION));
            }
            if (!isCount) {
                builder.append(SQLHelper.toLimitOffset(result.getPageNumber(), result.getPageSize()));
            }
        }
        builder.append(" ) as filter on (filter.id = u.id) ");
        if (!isCount) {
            builder.append(" left join user_role ur on (ur.user_id = u.id) ");
            builder.append(" left join role r on (r.id = ur.role_id) ");
            builder.append(" left join role_permission rp on (rp.role_id = ur.role_id) ");
            builder.append(" left join permission p on (p.id = rp.permission_id) ");
        }
        builder.append(" where 1=1 ");
        if (!isCount) {
            builder.append(" group by u.id ");
        }
        if (isCount) {
            Query query = entityManager.createNativeQuery(builder.toString());
            SQLHelper.setParams(query, params);
            long count = ConversionUtils.safeToLong(query.getSingleResult());
            result.setTotalItems(count);
            result.setTotalPages(SQLHelper.toPages(count, result.getPageSize()));
        } else {
            Query query = entityManager.createNativeQuery(builder.toString(), ResultSetName.USER_SEARCH);
            SQLHelper.setParams(query, params);
            List<UserDTO> items = query.getResultList();
            result.setItems(items);
        }
        return result;
    }

}
