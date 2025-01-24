package com.example.springboot.modules.configuration;

import com.example.springboot.dtos.PaginationResponseData;
import com.example.springboot.exception.CustomException;
import com.example.springboot.modules.configuration.dtos.ConfigurationDTO;
import com.example.springboot.modules.configuration.dtos.SearchConfigurationRequestDTO;

public interface ConfigurationService {

    ConfigurationDTO save(ConfigurationDTO requestDTO);

    ConfigurationDTO findOneById(Long id);

    ConfigurationDTO findOneByIdOrThrow(Long id);

    ConfigurationDTO findOneByCode(String code);

    ConfigurationDTO findOneByCodeOrThrow(String code);

    String findValueByCode(String code);

    PaginationResponseData<ConfigurationDTO> search(SearchConfigurationRequestDTO requestDTO, boolean isCount);

}
