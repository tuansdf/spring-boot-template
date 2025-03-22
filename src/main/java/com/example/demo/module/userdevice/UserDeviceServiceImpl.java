package com.example.demo.module.userdevice;

import com.example.demo.common.constant.CommonStatus;
import com.example.demo.common.dto.RequestContextHolder;
import com.example.demo.common.mapper.CommonMapper;
import com.example.demo.module.userdevice.dto.UserDeviceDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
            Optional<UserDevice> userDeviceOptional = userDeviceRepository.findById(userDeviceDTO.getId());
            if (userDeviceOptional.isPresent()) {
                result = userDeviceOptional.get();
            }
        }
        if (result == null) {
            result = new UserDevice();
            result.setUserId(RequestContextHolder.get().getUserId());
            result.setStatus(CommonStatus.ACTIVE);
        }
        result.setFcmToken(userDeviceDTO.getFcmToken());
        return commonMapper.toDTO(userDeviceRepository.save(result));
    }

    @Override
    public UserDeviceDTO findOneById(UUID id) {
        return userDeviceRepository.findById(id).map(commonMapper::toDTO).orElse(null);
    }

    @Override
    public List<UserDeviceDTO> findAllByUserId(UUID userId) {
        List<UserDevice> userDevices = userDeviceRepository.findAllByUserId(userId);
        if (userDevices.isEmpty()) {
            return new ArrayList<>();
        }
        return userDevices.stream().map(commonMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<String> findAllTokensByUserId(UUID userId) {
        return userDeviceRepository.findAllTokensByUserId(userId);
    }

}
