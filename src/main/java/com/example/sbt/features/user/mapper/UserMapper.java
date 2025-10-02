package com.example.sbt.features.user.mapper;

import com.example.sbt.features.user.dto.UserDTO;
import com.example.sbt.features.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDTO toDTO(User user);

    User toEntity(UserDTO user);
}
