package com.example.springboot.modules.configuration;

import com.example.springboot.dtos.PaginationResponseData;
import com.example.springboot.exception.CustomException;
import com.example.springboot.modules.configuration.dtos.ConfigurationDTO;
import com.example.springboot.modules.configuration.dtos.SearchConfigurationRequestDTO;

public interface ConfigurationService {

    ConfigurationDTO save(ConfigurationDTO requestDTO) throws CustomException;

    ConfigurationDTO findOneById(Long id);

    ConfigurationDTO findOneByIdOrThrow(Long id) throws CustomException;

    ConfigurationDTO findOneByCode(String code);

    ConfigurationDTO findOneByCodeOrThrow(String code) throws CustomException;

    String findValueByCode(String code);

    PaginationResponseData<ConfigurationDTO> search(SearchConfigurationRequestDTO requestDTO, boolean isCountOnly);

}
