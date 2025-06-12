package com.example.sbt.module.user;

import com.example.sbt.common.dto.PaginationData;
import com.example.sbt.module.user.dto.SearchUserRequestDTO;
import com.example.sbt.module.user.dto.UserDTO;

import java.util.UUID;

public interface UserService {

    UserDTO updateProfile(UserDTO requestDTO);

    UserDTO findOneById(UUID userId);

    UserDTO findOneByIdOrThrow(UUID userId);

    UserDTO findOneByUsername(String username);

    UserDTO findOneByUsernameOrThrow(String username);

    UserDTO findOneByEmail(String email);

    UserDTO findOneByEmailOrThrow(String email);

    PaginationData<UserDTO> search(SearchUserRequestDTO requestDTO, boolean isCount);

}
