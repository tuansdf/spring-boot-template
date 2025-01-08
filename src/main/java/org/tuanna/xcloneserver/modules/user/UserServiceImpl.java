package org.tuanna.xcloneserver.modules.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.tuanna.xcloneserver.dtos.PaginationResponseData;
import org.tuanna.xcloneserver.entities.User;
import org.tuanna.xcloneserver.exception.CustomException;
import org.tuanna.xcloneserver.mappers.CommonMapper;
import org.tuanna.xcloneserver.modules.user.dtos.SearchUserRequestDTO;
import org.tuanna.xcloneserver.modules.user.dtos.UserDTO;
import org.tuanna.xcloneserver.utils.ConversionUtils;
import org.tuanna.xcloneserver.utils.MapperListObjectUtils;
import org.tuanna.xcloneserver.utils.SQLUtils;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final CommonMapper commonMapper;
    private final EntityManager entityManager;
    private final UserRepository userRepository;

    @Override
    public UserDTO save(UserDTO requestDTO, UUID byUser) throws CustomException {
        requestDTO.validate();
        User result = null;
        if (requestDTO.getId() != null) {
            Optional<User> userOptional = userRepository.findById(requestDTO.getId());
            result = userOptional.orElse(null);
        }
        if (result == null) {
            result = new User();
            result.setCreatedBy(byUser);
        }
        result.setUsername(requestDTO.getUsername());
        result.setEmail(requestDTO.getEmail());
        result.setName(requestDTO.getName());
        result.setPassword(requestDTO.getPassword());
        result.setStatus(requestDTO.getStatus());
        result.setUpdatedBy(byUser);
        return commonMapper.toDTO(userRepository.save(result));
    }

    @Override
    public boolean existsByUsernameOrEmail(String username, String email) {
        return userRepository.existsByUsernameOrEmail(username, email);
    }

    @Override
    public UserDTO findOneById(UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.map(commonMapper::toDTO).orElse(null);
    }

    @Override
    public UserDTO findOneByUsername(String username) {
        Optional<User> userOptional = userRepository.findTopByUsername(username);
        return userOptional.map(commonMapper::toDTO).orElse(null);
    }

    @Override
    public UserDTO findOneByEmail(String email) {
        Optional<User> userOptional = userRepository.findTopByEmail(email);
        return userOptional.map(commonMapper::toDTO).orElse(null);
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
            builder.append(" select u.id, u.username ");
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
            Long count = ConversionUtils.toLong(query.getSingleResult());
            result.setTotalItems(count);
        } else {
            Query query = entityManager.createNativeQuery(builder.toString(), Tuple.class);
            SQLUtils.setParams(query, params);
            List<Tuple> tuples = query.getResultList();
            result.setItems(UserDTO.fromTuples(tuples));

//            Query query = entityManager.createNativeQuery(builder.toString(), "UserDTO");
//            SQLUtils.setParams(query, params);
//            List<UserDTO> tuples = query.getResultList();
//            result.setItems(tuples);

//            Query query = entityManager.createNativeQuery(builder.toString(), Tuple.class);
//            SQLUtils.setParams(query, params);
//            List<Tuple> tuples = query.getResultList();
//            result.setItems(MapperListObjectUtils.mapListOfObjects(tuples, UserDTO.class));
        }
        return result;
    }

}
