package org.tuanna.xcloneserver.modules.user;

import org.tuanna.xcloneserver.modules.user.dtos.UserDTO;

import java.util.UUID;

public interface UserService {

    UserDTO save(UserDTO userDTO);

    boolean existsByUsernameOrEmail(String username, String email);

    UserDTO findOneById(UUID userId);

    UserDTO findOneByUsername(String username);

    UserDTO findOneByEmail(String email);

}
