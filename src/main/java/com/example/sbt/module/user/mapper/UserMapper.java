package com.example.sbt.module.user.mapper;

import com.example.sbt.module.user.dto.UserDTO;
import com.example.sbt.module.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

    User toEntity(UserDTO user);

}
