package com.example.demo.module.user;

import com.example.demo.common.constant.CommonType;
import com.example.demo.common.constant.PermissionCode;
import com.example.demo.common.constant.ResultSetName;
import com.example.demo.common.dto.PaginationResponseData;
import com.example.demo.common.exception.CustomException;
import com.example.demo.common.mapper.CommonMapper;
import com.example.demo.common.util.AuthHelper;
import com.example.demo.common.util.ConversionUtils;
import com.example.demo.common.util.SQLBuilder;
import com.example.demo.module.token.TokenService;
import com.example.demo.module.user.dto.ChangePasswordRequestDTO;
import com.example.demo.module.user.dto.SearchUserRequestDTO;
import com.example.demo.module.user.dto.UserDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class UserServiceImpl implements UserService {

    private final CommonMapper commonMapper;
    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Override
    public UserDTO changePassword(ChangePasswordRequestDTO requestDTO, UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new CustomException(HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
        boolean isPasswordCorrect = passwordEncoder.matches(requestDTO.getOldPassword(), user.getPassword());
        if (!isPasswordCorrect) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        user.setPassword(passwordEncoder.encode(requestDTO.getNewPassword()));
        user = userRepository.save(user);
        tokenService.deactivatePastTokens(user.getId(), CommonType.REFRESH_TOKEN);
        return commonMapper.toDTO(user);
    }

    @Override
    public UserDTO updateProfile(UserDTO requestDTO) {
        requestDTO.validateUpdate();
        Optional<User> userOptional = userRepository.findById(requestDTO.getId());
        if (userOptional.isEmpty()) {
            throw new CustomException(HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
        if (!ConversionUtils.safeToString(user.getUsername()).equals(requestDTO.getUsername()) && existsByUsername(requestDTO.getUsername())) {
            throw new CustomException(HttpStatus.CONFLICT);
        }
        if (!ConversionUtils.safeToString(user.getEmail()).equals(requestDTO.getEmail()) && existsByEmail(requestDTO.getEmail())) {
            throw new CustomException(HttpStatus.CONFLICT);
        }
        user.setUsername(requestDTO.getUsername());
        user.setEmail(requestDTO.getEmail());
        user.setName(requestDTO.getName());
        if (AuthHelper.hasAnyPermission(List.of(PermissionCode.SYSTEM_ADMIN, PermissionCode.UPDATE_USER))) {
            user.setStatus(requestDTO.getStatus());
        }
        return commonMapper.toDTO(userRepository.save(user));
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
        return result.map(commonMapper::toDTO).orElse(null);
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
        return result.map(commonMapper::toDTO).orElse(null);
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
        return result.map(commonMapper::toDTO).orElse(null);
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
    public PaginationResponseData<UserDTO> search(SearchUserRequestDTO requestDTO, boolean isCount) {
        PaginationResponseData<UserDTO> result = executeSearch(requestDTO, true);
        if (!isCount && result.getTotalItems() > 0) {
            result.setItems(executeSearch(requestDTO, false).getItems());
        }
        return result;
    }

    private PaginationResponseData<UserDTO> executeSearch(SearchUserRequestDTO requestDTO, boolean isCount) {
        PaginationResponseData<UserDTO> result = SQLBuilder.getPaginationResponseData(requestDTO.getPageNumber(), requestDTO.getPageSize());
        Map<String, Object> params = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        if (isCount) {
            builder.append(" select count(*) ");
        } else {
            builder.append(" select u.*, ");
            builder.append(" string_agg(distinct(r.code), ',') as roles, ");
            builder.append(" string_agg(distinct(p.code), ',') as permissions ");
            builder.append(" null as roles, ");
            builder.append(" null as permissions ");
        }
        builder.append(" from _user u ");
        builder.append(" inner join ( ");
        {
            builder.append(" select u.id ");
            builder.append(" from _user u ");
            builder.append(" where 1=1 ");
            if (StringUtils.isNotBlank(requestDTO.getUsername())) {
                builder.append(" and u.username = :username ");
                params.put("username", StringUtils.stripStart(requestDTO.getUsername(), "%").concat("%"));
            }
            if (StringUtils.isNotBlank(requestDTO.getEmail())) {
                builder.append(" and u.email like :email ");
                params.put("email", StringUtils.stripStart(requestDTO.getEmail(), "%").concat("%"));
            }
            if (requestDTO.getStatus() != null) {
                builder.append(" and u.status = :status ");
                params.put("status", requestDTO.getStatus());
            }
            if (requestDTO.getCreatedAtFrom() != null) {
                builder.append(" and u.created_at >= :createdAtFrom ");
                params.put("createdAtFrom", requestDTO.getCreatedAtFrom());
            }
            if (requestDTO.getCreatedAtTo() != null) {
                builder.append(" and u.created_at <= :createdAtTo ");
                params.put("createdAtTo", requestDTO.getCreatedAtTo());
            }
            if (!isCount) {
                builder.append(SQLBuilder.getPaginationString(result.getPageNumber(), result.getPageSize()));
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
            SQLBuilder.setParams(query, params);
            long count = ConversionUtils.safeToLong(query.getSingleResult());
            result.setTotalItems(count);
            result.setTotalPages(SQLBuilder.getTotalPages(count, result.getPageSize()));
        } else {
            Query query = entityManager.createNativeQuery(builder.toString(), ResultSetName.USER_SEARCH);
            SQLBuilder.setParams(query, params);
            List<UserDTO> items = query.getResultList();
            result.setItems(items);
        }
        return result;
    }

}
