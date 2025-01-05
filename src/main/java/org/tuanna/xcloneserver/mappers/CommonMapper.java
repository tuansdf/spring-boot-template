package org.tuanna.xcloneserver.mappers;

import org.mapstruct.Mapper;
import org.tuanna.xcloneserver.entities.User;
import org.tuanna.xcloneserver.modules.user.dtos.UserDTO;

@Mapper(componentModel = "spring")
public interface CommonMapper {

    User userDTOToEntity(UserDTO userDTO);

    UserDTO userEntityToDTO(User user);

}
