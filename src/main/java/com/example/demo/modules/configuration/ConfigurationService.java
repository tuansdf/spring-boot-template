package com.example.demo.modules.configuration;

import com.example.demo.dtos.PaginationResponseData;
import com.example.demo.modules.configuration.dtos.ConfigurationDTO;
import com.example.demo.modules.configuration.dtos.SearchConfigurationRequestDTO;

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
