package com.example.demo.module.user;

import com.example.demo.common.dto.PaginationResponseData;
import com.example.demo.module.user.dto.ChangePasswordRequestDTO;
import com.example.demo.module.user.dto.SearchUserRequestDTO;
import com.example.demo.module.user.dto.UserDTO;

import java.util.UUID;

public interface UserService {

    UserDTO changePassword(ChangePasswordRequestDTO requestDTO, UUID userId);

    UserDTO updateProfile(UserDTO requestDTO);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsernameOrEmail(String username, String email);

    UserDTO findOneById(UUID userId);

    UserDTO findOneByIdOrThrow(UUID userId);

    UserDTO findOneByUsername(String username);

    UserDTO findOneByUsernameOrThrow(String username);

    UserDTO findOneByEmail(String email);

    UserDTO findOneByEmailOrThrow(String email);

    PaginationResponseData<UserDTO> search(SearchUserRequestDTO requestDTO, boolean isCount);

}
