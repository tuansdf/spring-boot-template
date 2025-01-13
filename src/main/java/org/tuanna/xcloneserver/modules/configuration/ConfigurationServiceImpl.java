package org.tuanna.xcloneserver.modules.configuration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.tuanna.xcloneserver.constants.ResultSetName;
import org.tuanna.xcloneserver.constants.Status;
import org.tuanna.xcloneserver.dtos.PaginationResponseData;
import org.tuanna.xcloneserver.entities.Configuration;
import org.tuanna.xcloneserver.exception.CustomException;
import org.tuanna.xcloneserver.mappers.CommonMapper;
import org.tuanna.xcloneserver.modules.configuration.dtos.ConfigurationDTO;
import org.tuanna.xcloneserver.modules.configuration.dtos.SearchConfigurationRequestDTO;
import org.tuanna.xcloneserver.utils.CommonUtils;
import org.tuanna.xcloneserver.utils.ConversionUtils;
import org.tuanna.xcloneserver.utils.SQLUtils;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class ConfigurationServiceImpl implements ConfigurationService {

    private final CommonMapper commonMapper;
    private final EntityManager entityManager;
    private final ConfigurationRepository configurationRepository;

    @Override
    public ConfigurationDTO save(ConfigurationDTO requestDTO, UUID actionBy) throws CustomException {
        Configuration result = null;
        if (requestDTO.getId() != null) {
            Optional<Configuration> configurationOptional = configurationRepository.findById(requestDTO.getId());
            if (configurationOptional.isPresent()) {
                requestDTO.validateUpdate();
                result = configurationOptional.get();
            }
        }
        if (result == null) {
            requestDTO.validateCreate();
            String code = ConversionUtils.toCode(requestDTO.getCode());
            if (configurationRepository.existsByCode(code)) {
                throw new CustomException(HttpStatus.CONFLICT);
            }
            result = new Configuration();
            result.setCode(code);
            result.setCreatedBy(actionBy);
        }
        result.setValue(requestDTO.getValue());
        result.setDescription(requestDTO.getDescription());
        if (StringUtils.isBlank(requestDTO.getStatus())) {
            result.setStatus(Status.ACTIVE);
        } else {
            result.setStatus(ConversionUtils.toCode(requestDTO.getStatus()));
        }
        result.setUpdatedBy(actionBy);
        return commonMapper.toDTO(configurationRepository.save(result));
    }

    @Override
    public ConfigurationDTO findOneById(Long id) {
        Optional<Configuration> configurationOptional = configurationRepository.findById(id);
        return commonMapper.toDTO(configurationOptional.orElse(null));
    }

    @Override
    public ConfigurationDTO findOneByIdOrThrow(Long id) throws CustomException {
        ConfigurationDTO result = findOneById(id);
        if (result == null) {
            throw new CustomException(HttpStatus.NOT_FOUND);
        }
        return result;
    }

    @Override
    public ConfigurationDTO findOneByCode(String code) {
        Optional<Configuration> configurationOptional = configurationRepository.findTopByCode(code);
        return commonMapper.toDTO(configurationOptional.orElse(null));
    }

    @Override
    public ConfigurationDTO findOneByCodeOrThrow(String code) throws CustomException {
        ConfigurationDTO result = findOneByCode(code);
        if (result == null) {
            throw new CustomException(HttpStatus.NOT_FOUND);
        }
        return result;
    }

    @Override
    public String findValueByCode(String code) {
        return configurationRepository.findTopValueByCodeAndStatus(code, Status.ACTIVE);
    }

    @Override
    public Boolean findBooleanValueByCode(String code) {
        String result = configurationRepository.findTopValueByCodeAndStatus(code, Status.ACTIVE);
        if (result == null) {
            return null;
        }
        return CommonUtils.isTrue(result);
    }

    @Override
    public PaginationResponseData<ConfigurationDTO> search(SearchConfigurationRequestDTO requestDTO, boolean isCountOnly) {
        PaginationResponseData<ConfigurationDTO> result = executeSearch(requestDTO, true);
        if (!isCountOnly && result.getTotalItems() > 0) {
            result.setItems(executeSearch(requestDTO, false).getItems());
        }
        return result;
    }

    private PaginationResponseData<ConfigurationDTO> executeSearch(SearchConfigurationRequestDTO requestDTO, boolean isCount) {
        PaginationResponseData<ConfigurationDTO> result = SQLUtils.getPaginationResponseData(requestDTO.getPageNumber(), requestDTO.getPageSize());
        Map<String, Object> params = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        if (isCount) {
            builder.append(" select count(*) ");
        } else {
            builder.append(" select c.* ");
        }
        builder.append(" from configuration c ");
        builder.append(" where 1=1 ");
        if (!StringUtils.isEmpty(requestDTO.getCode())) {
            builder.append(" and c.code = :code ");
            params.put("code", requestDTO.getCode());
        }
        if (!StringUtils.isEmpty(requestDTO.getStatus())) {
            builder.append(" and c.status = :status ");
            params.put("status", requestDTO.getStatus());
        }
        if (requestDTO.getCreatedAtFrom() != null) {
            builder.append(" and c.created_at >= :createdAtFrom ");
            params.put("createdAtFrom", requestDTO.getCreatedAtFrom());
        }
        if (requestDTO.getCreatedAtTo() != null) {
            builder.append(" and c.created_at <= :createdAtTo ");
            params.put("createdAtTo", requestDTO.getCreatedAtTo());
        }
        if (!isCount) {
            builder.append(SQLUtils.getPaginationString(result.getPageNumber(), result.getPageSize()));
        }
        if (isCount) {
            Query query = entityManager.createNativeQuery(builder.toString());
            SQLUtils.setParams(query, params);
            long count = ConversionUtils.safeToLong(query.getSingleResult());
            result.setTotalItems(count);
            result.setTotalPages(SQLUtils.getTotalPages(count, result.getPageSize()));
        } else {
            Query query = entityManager.createNativeQuery(builder.toString(), ResultSetName.CONFIGURATION_SEARCH);
            SQLUtils.setParams(query, params);
            List<ConfigurationDTO> items = query.getResultList();
            result.setItems(items);
        }
        return result;
    }

}
