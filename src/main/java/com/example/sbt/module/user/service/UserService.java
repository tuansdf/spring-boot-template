package com.example.sbt.module.user.service;

import com.example.sbt.core.dto.PaginationData;
import com.example.sbt.module.user.dto.SearchUserRequest;
import com.example.sbt.module.user.dto.UserDTO;

import java.io.IOException;
import java.util.UUID;

public interface UserService {
    UserDTO updateProfile(UserDTO requestDTO);

    UserDTO findOneById(UUID userId);

    UserDTO findOneByIdOrThrow(UUID userId);

    UserDTO findOneByUsername(String username);

    UserDTO findOneByEmail(String email);

    PaginationData<UserDTO> search(SearchUserRequest requestDTO, boolean isCount);

    void triggerExport(SearchUserRequest requestDTO);

    void handleExportTask(UUID backgroundTaskId, SearchUserRequest requestDTO) throws IOException;
}
