package org.tuanna.xcloneserver.modules.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.tuanna.xcloneserver.constants.PermissionCode;
import org.tuanna.xcloneserver.constants.ResultSetName;
import org.tuanna.xcloneserver.constants.CommonType;
import org.tuanna.xcloneserver.dtos.PaginationResponseData;
import org.tuanna.xcloneserver.entities.User;
import org.tuanna.xcloneserver.exception.CustomException;
import org.tuanna.xcloneserver.mappers.CommonMapper;
import org.tuanna.xcloneserver.modules.token.TokenService;
import org.tuanna.xcloneserver.modules.user.dtos.ChangePasswordRequestDTO;
import org.tuanna.xcloneserver.modules.user.dtos.SearchUserRequestDTO;
import org.tuanna.xcloneserver.modules.user.dtos.UserDTO;
import org.tuanna.xcloneserver.utils.AuthUtils;
import org.tuanna.xcloneserver.utils.ConversionUtils;
import org.tuanna.xcloneserver.utils.SQLUtils;

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
    public UserDTO changePassword(ChangePasswordRequestDTO requestDTO, UUID userId) throws CustomException {
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
        user.setUpdatedBy(userId);
        user = userRepository.save(user);
        tokenService.deactivatePastTokens(user.getId(), CommonType.REFRESH_TOKEN);
        return commonMapper.toDTO(user);
    }

    @Override
    public UserDTO updateProfile(UserDTO requestDTO, UUID actionBy) throws CustomException {
        requestDTO.validateUpdate();
        Optional<User> userOptional = userRepository.findById(requestDTO.getId());
        if (userOptional.isEmpty()) {
            throw new CustomException(HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
        if (!ConversionUtils.toString(user.getUsername()).equals(requestDTO.getUsername()) && existsByUsername(requestDTO.getUsername())) {
            throw new CustomException(HttpStatus.CONFLICT);
        }
        if (!ConversionUtils.toString(user.getEmail()).equals(requestDTO.getEmail()) && existsByEmail(requestDTO.getEmail())) {
            throw new CustomException(HttpStatus.CONFLICT);
        }
        user.setUsername(requestDTO.getUsername());
        user.setEmail(requestDTO.getEmail());
        user.setName(requestDTO.getName());
        if (AuthUtils.hasAnyPermission(List.of(PermissionCode.SYSTEM_ADMIN, PermissionCode.UPDATE_USER))) {
            user.setStatus(requestDTO.getStatus());
        }
        user.setUpdatedBy(actionBy);
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
    public UserDTO findOneByIdOrThrow(UUID userId) throws CustomException {
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
    public UserDTO findOneByUsernameOrThrow(String username) throws CustomException {
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
    public UserDTO findOneByEmailOrThrow(String email) throws CustomException {
        UserDTO result = findOneByEmail(email);
        if (result == null) {
            throw new CustomException(HttpStatus.NOT_FOUND);
        }
        return result;
    }

    @Override
    public PaginationResponseData<UserDTO> search(SearchUserRequestDTO requestDTO, boolean isCountOnly) {
        PaginationResponseData<UserDTO> result = executeSearch(requestDTO, true);
        if (!isCountOnly && result.getTotalItems() > 0) {
            result.setItems(executeSearch(requestDTO, false).getItems());
        }
        return result;
    }

    private PaginationResponseData<UserDTO> executeSearch(SearchUserRequestDTO requestDTO, boolean isCount) {
        PaginationResponseData<UserDTO> result = SQLUtils.getPaginationResponseData(requestDTO.getPageNumber(), requestDTO.getPageSize());
        Map<String, Object> params = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        if (isCount) {
            builder.append(" select count(*) ");
        } else {
            builder.append(" select u.* ");
        }
        builder.append(" from _user u ");
        builder.append(" where 1=1 ");
        if (!StringUtils.isEmpty(requestDTO.getUsername())) {
            builder.append(" and u.username = :username ");
            params.put("username", requestDTO.getUsername().concat("%"));
        }
        if (!StringUtils.isEmpty(requestDTO.getEmail())) {
            builder.append(" and u.email like :email ");
            params.put("email", requestDTO.getEmail().concat("%"));
        }
        if (!StringUtils.isEmpty(requestDTO.getStatus())) {
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
            builder.append(SQLUtils.getPaginationString(result.getPageNumber(), result.getPageSize()));
        }
        if (isCount) {
            Query query = entityManager.createNativeQuery(builder.toString());
            SQLUtils.setParams(query, params);
            long count = ConversionUtils.safeToLong(query.getSingleResult());
            result.setTotalItems(count);
            result.setTotalPages(SQLUtils.getTotalPages(count, result.getPageSize()));
        } else {
            Query query = entityManager.createNativeQuery(builder.toString(), ResultSetName.USER_SEARCH);
            SQLUtils.setParams(query, params);
            List<UserDTO> items = query.getResultList();
            result.setItems(items);
        }
        return result;
    }

}
