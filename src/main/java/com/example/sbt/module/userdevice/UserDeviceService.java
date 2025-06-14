package com.example.sbt.module.userdevice;

import com.example.sbt.module.userdevice.dto.UserDeviceDTO;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserDeviceService {

    UserDeviceDTO save(UserDeviceDTO userDeviceDTO);

    UserDeviceDTO findOneById(UUID id);

    List<UserDeviceDTO> findAllByUserId(UUID userId);

    Set<String> findAllTokensByUserId(UUID userId);

}
