package com.example.sbt.features.configuration.service;

import com.example.sbt.common.dto.PaginationData;
import com.example.sbt.features.configuration.dto.ConfigurationDTO;
import com.example.sbt.features.configuration.dto.SearchConfigurationRequest;

import java.util.List;
import java.util.Map;

public interface ConfigurationService {
    ConfigurationDTO save(ConfigurationDTO requestDTO);

    ConfigurationDTO findOneByCode(String code);

    ConfigurationDTO findOneByCodeOrThrow(String code);

    String findValueByCode(String code);

    Map<String, String> findPublicValuesByCodes(List<String> codes);

    PaginationData<ConfigurationDTO> search(SearchConfigurationRequest requestDTO, boolean isCount);
}
