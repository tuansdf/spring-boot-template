package org.tuanna.xcloneserver.modules.user;

import org.tuanna.xcloneserver.dtos.PaginationResponseData;
import org.tuanna.xcloneserver.exception.CustomException;
import org.tuanna.xcloneserver.modules.user.dtos.SearchUserRequestDTO;
import org.tuanna.xcloneserver.modules.user.dtos.UserDTO;

import java.util.UUID;

public interface UserService {

    UserDTO save(UserDTO userDTO, UUID byUser) throws CustomException;

    boolean existsByUsernameOrEmail(String username, String email);

    UserDTO findOneById(UUID userId);

    UserDTO findOneByUsername(String username);

    UserDTO findOneByEmail(String email);

    PaginationResponseData<UserDTO> search(SearchUserRequestDTO requestDTO, boolean isCountOnly);
}
