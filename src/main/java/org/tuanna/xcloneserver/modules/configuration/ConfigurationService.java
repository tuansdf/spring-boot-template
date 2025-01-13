package org.tuanna.xcloneserver.modules.configuration;

import org.tuanna.xcloneserver.modules.configuration.dtos.ConfigurationDTO;

public interface ConfigurationService {

    ConfigurationDTO findOneById(Long id);

    String findValueByCode(String code);

    Boolean findBooleanValueByCode(String code);
}
