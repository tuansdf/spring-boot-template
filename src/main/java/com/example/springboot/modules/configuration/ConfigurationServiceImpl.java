package com.example.springboot.modules.configuration;

import com.example.springboot.configs.RequestContextHolder;
import com.example.springboot.constants.CommonStatus;
import com.example.springboot.constants.ResultSetName;
import com.example.springboot.dtos.PaginationResponseData;
import com.example.springboot.entities.Configuration;
import com.example.springboot.exception.CustomException;
import com.example.springboot.mappers.CommonMapper;
import com.example.springboot.modules.configuration.dtos.ConfigurationDTO;
import com.example.springboot.modules.configuration.dtos.SearchConfigurationRequestDTO;
import com.example.springboot.utils.ConversionUtils;
import com.example.springboot.utils.SQLBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class ConfigurationServiceImpl implements ConfigurationService {

    private final CommonMapper commonMapper;
    private final EntityManager entityManager;
    private final ConfigurationRepository configurationRepository;
    private final ConfigurationValidator configurationValidator;

    @Override
    public ConfigurationDTO save(ConfigurationDTO requestDTO) {
        UUID actionBy = ConversionUtils.toUUID(RequestContextHolder.get().getUserId());
        Configuration result = null;
        if (requestDTO.getId() != null) {
            Optional<Configuration> configurationOptional = configurationRepository.findById(requestDTO.getId());
            if (configurationOptional.isPresent()) {
                configurationValidator.validateUpdate(requestDTO);
                result = configurationOptional.get();
            }
        }
        if (result == null) {
            configurationValidator.validateCreate(requestDTO);
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
            result.setStatus(CommonStatus.ACTIVE);
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
    public ConfigurationDTO findOneByIdOrThrow(Long id) {
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
    public ConfigurationDTO findOneByCodeOrThrow(String code) {
        ConfigurationDTO result = findOneByCode(code);
        if (result == null) {
            throw new CustomException(HttpStatus.NOT_FOUND);
        }
        return result;
    }

    @Override
    public String findValueByCode(String code) {
        return configurationRepository.findTopValueByCodeAndStatus(code, CommonStatus.ACTIVE);
    }

    @Override
    public PaginationResponseData<ConfigurationDTO> search(SearchConfigurationRequestDTO requestDTO, boolean isCount) {
        PaginationResponseData<ConfigurationDTO> result = executeSearch(requestDTO, true);
        if (!isCount && result.getTotalItems() > 0) {
            result.setItems(executeSearch(requestDTO, false).getItems());
        }
        return result;
    }

    private PaginationResponseData<ConfigurationDTO> executeSearch(SearchConfigurationRequestDTO requestDTO, boolean isCount) {
        PaginationResponseData<ConfigurationDTO> result = SQLBuilder.getPaginationResponseData(requestDTO.getPageNumber(), requestDTO.getPageSize());
        Map<String, Object> params = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        if (isCount) {
            builder.append(" select count(*) ");
        } else {
            builder.append(" select c.* ");
        }
        builder.append(" from configuration c ");
        builder.append(" where 1=1 ");
        if (StringUtils.isNotBlank(requestDTO.getCode())) {
            builder.append(" and c.code = :code ");
            params.put("code", requestDTO.getCode());
        }
        if (StringUtils.isNotBlank(requestDTO.getStatus())) {
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
            builder.append(SQLBuilder.getPaginationString(result.getPageNumber(), result.getPageSize()));
        }
        if (isCount) {
            Query query = entityManager.createNativeQuery(builder.toString());
            SQLBuilder.setParams(query, params);
            long count = ConversionUtils.safeToLong(query.getSingleResult());
            result.setTotalItems(count);
            result.setTotalPages(SQLBuilder.getTotalPages(count, result.getPageSize()));
        } else {
            Query query = entityManager.createNativeQuery(builder.toString(), ResultSetName.CONFIGURATION_SEARCH);
            SQLBuilder.setParams(query, params);
            List<ConfigurationDTO> items = query.getResultList();
            result.setItems(items);
        }
        return result;
    }

}
