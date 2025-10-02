package com.example.sbt.features.userdevice.service;

import com.example.sbt.common.dto.RequestContext;
import com.example.sbt.features.userdevice.dto.UserDeviceDTO;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserDeviceService {
    UserDeviceDTO save(UserDeviceDTO userDeviceDTO, RequestContext requestContext);

    UserDeviceDTO findOneById(UUID id);

    List<UserDeviceDTO> findAllByUserId(UUID userId);

    Set<String> findAllTokensByUserId(UUID userId);
}
