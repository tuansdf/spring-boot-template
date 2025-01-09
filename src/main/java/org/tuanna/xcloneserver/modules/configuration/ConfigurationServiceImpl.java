package org.tuanna.xcloneserver.modules.configuration;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tuanna.xcloneserver.constants.Status;
import org.tuanna.xcloneserver.entities.Configuration;
import org.tuanna.xcloneserver.mappers.CommonMapper;
import org.tuanna.xcloneserver.modules.configuration.dtos.ConfigurationDTO;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
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
    public String findOneValueById(Long id) {
        Optional<Configuration> configurationOptional = configurationRepository.findById(id);
        if (configurationOptional.isEmpty()) {
            return null;
        }
        Configuration configuration = configurationOptional.get();
        if (!Status.ACTIVE.equals(configuration.getStatus())) {
            return null;
        }
        return configuration.getValue();
    }

}
