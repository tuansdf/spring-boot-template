package org.tuanna.xcloneserver.modules.configuration;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tuanna.xcloneserver.constants.Status;
import org.tuanna.xcloneserver.entities.Configuration;
import org.tuanna.xcloneserver.mappers.CommonMapper;
import org.tuanna.xcloneserver.modules.configuration.dtos.ConfigurationDTO;
import org.tuanna.xcloneserver.utils.CommonUtils;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class ConfigurationServiceImpl implements ConfigurationService {

    private final CommonMapper commonMapper;
    private final EntityManager entityManager;
    private final ConfigurationRepository configurationRepository;

    @Override
    public ConfigurationDTO findOneById(Long id) {
        Optional<Configuration> configurationOptional = configurationRepository.findById(id);
        return commonMapper.toDTO(configurationOptional.orElse(null));
    }

    @Override
    public String findValueByCode(String code) {
        return configurationRepository.findTopValueByCodeAndStatus(code, Status.ACTIVE);
    }

    @Override
    public Boolean findBooleanValueByCode(String code) {
        String result = configurationRepository.findTopValueByCodeAndStatus(code, Status.ACTIVE);
        if (result == null) {
            return null;
        }
        return CommonUtils.isTrue(result);
    }

}
