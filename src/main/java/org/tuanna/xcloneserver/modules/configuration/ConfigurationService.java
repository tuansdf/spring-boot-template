package org.tuanna.xcloneserver.modules.configuration;

import org.tuanna.xcloneserver.dtos.PaginationResponseData;
import org.tuanna.xcloneserver.exception.CustomException;
import org.tuanna.xcloneserver.modules.configuration.dtos.ConfigurationDTO;
import org.tuanna.xcloneserver.modules.configuration.dtos.SearchConfigurationRequestDTO;

import java.util.UUID;

public interface ConfigurationService {

    ConfigurationDTO save(ConfigurationDTO requestDTO, UUID actionBy) throws CustomException;

    ConfigurationDTO findOneById(Long id);

    ConfigurationDTO findOneByCode(String code);

    String findValueByCode(String code);

    Boolean findBooleanValueByCode(String code);

    PaginationResponseData<ConfigurationDTO> search(SearchConfigurationRequestDTO requestDTO, boolean isCountOnly);

}
