package com.example.sbt.module.remoteconfig;

import com.example.sbt.common.dto.PaginationData;
import com.example.sbt.module.remoteconfig.dto.RemoteConfigDTO;
import com.example.sbt.module.remoteconfig.dto.SearchRemoteConfigRequestDTO;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface RemoteConfigService {

    RemoteConfigDTO save(RemoteConfigDTO requestDTO);

    RemoteConfigDTO findOneById(UUID id);

    RemoteConfigDTO findOneByIdOrThrow(UUID id);

    RemoteConfigDTO findOneByCode(String code);

    RemoteConfigDTO findOneByCodeOrThrow(String code);

    String findValueByCode(String code);

    Map<String, String> findPublicValues(Set<String> codes);

    PaginationData<RemoteConfigDTO> search(SearchRemoteConfigRequestDTO requestDTO, boolean isCount);

}
