package com.example.springboot.modules.user;

import com.example.springboot.dtos.PaginationResponseData;
import com.example.springboot.exception.CustomException;
import com.example.springboot.modules.user.dtos.ChangePasswordRequestDTO;
import com.example.springboot.modules.user.dtos.SearchUserRequestDTO;
import com.example.springboot.modules.user.dtos.UserDTO;

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
