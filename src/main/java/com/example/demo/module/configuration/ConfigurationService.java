package com.example.demo.module.configuration;

import com.example.demo.dto.PaginationResponseData;
import com.example.demo.module.configuration.dto.ConfigurationDTO;
import com.example.demo.module.configuration.dto.SearchConfigurationRequestDTO;

import java.util.UUID;

public interface ConfigurationService {

    ConfigurationDTO save(ConfigurationDTO requestDTO);

    ConfigurationDTO findOneById(UUID id);

    ConfigurationDTO findOneByIdOrThrow(UUID id);

    ConfigurationDTO findOneByCode(String code);

    ConfigurationDTO findOneByCodeOrThrow(String code);

    String findValueByCode(String code);

    PaginationResponseData<ConfigurationDTO> search(SearchConfigurationRequestDTO requestDTO, boolean isCount);

}
