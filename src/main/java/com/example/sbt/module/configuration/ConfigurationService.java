package com.example.sbt.module.configuration;

import com.example.sbt.common.dto.PaginationData;
import com.example.sbt.module.configuration.dto.ConfigurationDTO;
import com.example.sbt.module.configuration.dto.SearchConfigurationRequestDTO;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ConfigurationService {

    ConfigurationDTO save(ConfigurationDTO requestDTO);

    ConfigurationDTO findOneById(UUID id);

    ConfigurationDTO findOneByIdOrThrow(UUID id);

    ConfigurationDTO findOneByCode(String code);

    ConfigurationDTO findOneByCodeOrThrow(String code);

    String findValueByCode(String code);

    Map<String, String> findPublicValues(Set<String> codes);

    PaginationData<ConfigurationDTO> search(SearchConfigurationRequestDTO requestDTO, boolean isCount);

}
