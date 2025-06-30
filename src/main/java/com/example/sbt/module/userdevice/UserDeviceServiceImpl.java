package com.example.sbt.module.userdevice;

import com.example.sbt.common.dto.RequestContext;
import com.example.sbt.common.mapper.CommonMapper;
import com.example.sbt.module.userdevice.dto.UserDeviceDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class UserDeviceServiceImpl implements UserDeviceService {

    private final CommonMapper commonMapper;
    private final UserDeviceRepository userDeviceRepository;

    @Override
    public UserDeviceDTO save(UserDeviceDTO userDeviceDTO) {
        UserDevice result = null;
        if (userDeviceDTO.getId() != null) {
            result = userDeviceRepository.findById(userDeviceDTO.getId()).orElse(null);
        }
        if (result == null) {
            result = new UserDevice();
            result.setUserId(RequestContext.get().getUserId());
        }
        result.setFcmToken(userDeviceDTO.getFcmToken());
        return commonMapper.toDTO(userDeviceRepository.save(result));
    }

    @Override
    public UserDeviceDTO findOneById(UUID id) {
        if (id == null) return null;
        return userDeviceRepository.findById(id).map(commonMapper::toDTO).orElse(null);
    }

    @Override
    public List<UserDeviceDTO> findAllByUserId(UUID userId) {
        if (userId == null) return new ArrayList<>();
        List<UserDevice> userDevices = userDeviceRepository.findAllByUserId(userId);
        if (userDevices.isEmpty()) return new ArrayList<>();
        return userDevices.stream().map(commonMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<String> findAllTokensByUserId(UUID userId) {
        if (userId == null) return new ArrayList<>();
        return userDeviceRepository.findAllTokensByUserId(userId);
    }

}
