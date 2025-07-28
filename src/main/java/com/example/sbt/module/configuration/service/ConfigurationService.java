package com.example.sbt.module.configuration.service;

import com.example.sbt.core.dto.PaginationData;
import com.example.sbt.module.configuration.dto.ConfigurationDTO;
import com.example.sbt.module.configuration.dto.SearchConfigurationRequestDTO;
import com.example.sbt.module.configuration.entity.ConfigurationKV;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ConfigurationService {
    ConfigurationDTO save(ConfigurationDTO requestDTO);

    ConfigurationDTO findOneById(UUID id);

    ConfigurationDTO findOneByIdOrThrow(UUID id);

    ConfigurationDTO findOneByCode(String code);

    ConfigurationDTO findOneByCodeOrThrow(String code);

    ConfigurationKV findOneCachedByCode(String code);

    String findPublicValueByCode(String code);

    String findValueByCode(String code);

    Map<String, String> findPublicValuesByCodes(List<String> codes);

    PaginationData<ConfigurationDTO> search(SearchConfigurationRequestDTO requestDTO, boolean isCount);
}
