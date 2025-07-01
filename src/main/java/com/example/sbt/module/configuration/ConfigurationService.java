package com.example.sbt.module.configuration;

import com.example.sbt.core.dto.PaginationData;
import com.example.sbt.module.configuration.dto.ConfigurationDTO;
import com.example.sbt.module.configuration.dto.SearchConfigurationRequestDTO;

import java.util.List;
import java.util.Map;

public interface ConfigurationService {

    ConfigurationDTO save(ConfigurationDTO requestDTO);

    ConfigurationDTO findOneByCode(String code);

    ConfigurationDTO findOneByCodeOrThrow(String code);

    String findValueByCode(String code);

    Map<String, String> findPublicValues(List<String> codes);

    PaginationData<ConfigurationDTO> search(SearchConfigurationRequestDTO requestDTO, boolean isCount);

}
