package org.tuanna.xcloneserver.modules.user;

import org.tuanna.xcloneserver.dtos.PaginationResponseData;
import org.tuanna.xcloneserver.exception.CustomException;
import org.tuanna.xcloneserver.modules.user.dtos.ChangePasswordRequestDTO;
import org.tuanna.xcloneserver.modules.user.dtos.SearchUserRequestDTO;
import org.tuanna.xcloneserver.modules.user.dtos.UserDTO;

import java.util.Locale;
import java.util.UUID;

public interface UserService {

    UserDTO saveRaw(UserDTO requestDTO);

    UserDTO changePassword(ChangePasswordRequestDTO requestDTO, UUID actionBy) throws CustomException;

    UserDTO updatePassword(UUID userId, String password, UUID actionBy, Locale locale) throws CustomException;

    UserDTO updateProfile(UserDTO requestDTO, UUID actionBy) throws CustomException;

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsernameOrEmail(String username, String email);

    UserDTO findOneById(UUID userId);

    UserDTO findOneByIdOrThrow(UUID userId) throws CustomException;

    UserDTO findOneByUsername(String username);

    UserDTO findOneByUsernameOrThrow(String username) throws CustomException;

    UserDTO findOneByEmail(String email);

    UserDTO findOneByEmailOrThrow(String email) throws CustomException;

    PaginationResponseData<UserDTO> search(SearchUserRequestDTO requestDTO, boolean isCountOnly);

}
