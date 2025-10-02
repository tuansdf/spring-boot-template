package com.example.sbt.features.configuration.service;

import com.example.sbt.common.constant.CacheKey;
import com.example.sbt.common.dto.PaginationData;
import com.example.sbt.common.mapper.CommonMapper;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.common.util.DateUtils;
import com.example.sbt.infrastructure.exception.CustomException;
import com.example.sbt.infrastructure.persistence.SQLHelper;
import com.example.sbt.features.configuration.dto.ConfigurationDTO;
import com.example.sbt.features.configuration.dto.SearchConfigurationRequest;
import com.example.sbt.features.configuration.entity.Configuration;
import com.example.sbt.features.configuration.repository.ConfigurationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class ConfigurationServiceImpl implements ConfigurationService {
    private final SQLHelper sqlHelper;
    private final CommonMapper commonMapper;
    private final EntityManager entityManager;
    private final ConfigurationRepository configurationRepository;
    private final ConfigurationValidator configurationValidator;

    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheKey.CONFIGURATION_VALUES, key = "#requestDTO.code"),
            @CacheEvict(value = CacheKey.CONFIGURATION_MAPS, allEntries = true),
    })
    public ConfigurationDTO save(ConfigurationDTO requestDTO) {
        configurationValidator.cleanRequest(requestDTO);
        configurationValidator.validateUpdate(requestDTO);
        Configuration result = null;
        if (requestDTO.getId() != null) {
            result = configurationRepository.findById(requestDTO.getId()).orElse(null);
        }
        if (result == null) {
            configurationValidator.validateCreate(requestDTO);
            result = new Configuration();
            result.setCode(requestDTO.getCode());
        }
        result.setValue(requestDTO.getValue());
        result.setDescription(requestDTO.getDescription());
        result.setIsEnabled(requestDTO.getIsEnabled());
        result.setIsPublic(requestDTO.getIsPublic());
        result = configurationRepository.save(result);
        return commonMapper.toDTO(result);
    }

    @Override
    public ConfigurationDTO findOneByCode(String code) {
        if (StringUtils.isBlank(code)) return null;
        return configurationRepository.findTopByCode(code).map(commonMapper::toDTO).orElse(null);
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
    @Cacheable(value = CacheKey.CONFIGURATION_VALUES, key = "#code", sync = true)
    public String findValueByCode(String code) {
        ConfigurationDTO result = findOneByCode(code);
        if (result == null || !ConversionUtils.safeToBoolean(result.getIsEnabled())) {
            return null;
        }
        return ConversionUtils.safeToString(result.getValue());
    }

    @Override
    @Cacheable(value = CacheKey.CONFIGURATION_MAPS, key = "#codes", sync = true)
    public Map<String, String> findPublicValuesByCodes(List<String> codes) {
        Map<String, String> result = new HashMap<>();
        if (CollectionUtils.isEmpty(codes)) return result;
        List<Configuration> configurations = configurationRepository.findAllByCodeIn(codes);
        if (CollectionUtils.isEmpty(configurations)) return result;
        for (Configuration configuration : configurations) {
            if (!ConversionUtils.safeToBoolean(configuration.getIsEnabled()) || !ConversionUtils.safeToBoolean(configuration.getIsPublic())) {
                continue;
            }
            result.put(configuration.getCode(), configuration.getValue());
        }
        return result;
    }

    @Override
    public PaginationData<ConfigurationDTO> search(SearchConfigurationRequest requestDTO, boolean isCount) {
        PaginationData<ConfigurationDTO> result = executeSearch(requestDTO, true);
        if (!isCount && result.getTotalItems() > 0) {
            result.setItems(executeSearch(requestDTO, false).getItems());
        }
        return result;
    }

    private PaginationData<ConfigurationDTO> executeSearch(SearchConfigurationRequest requestDTO, boolean isCount) {
        PaginationData<ConfigurationDTO> result = sqlHelper.initData(requestDTO.getPageNumber(), requestDTO.getPageSize());
        List<Object> params = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        if (isCount) {
            builder.append(" select count(*) ");
        } else {
            builder.append(" select c.id, c.code, c.value, c.description, c.is_enabled, c.is_public, c.created_at, c.updated_at ");
        }
        builder.append(" from configuration c ");
        builder.append(" where 1=1 ");
        if (StringUtils.isNotBlank(requestDTO.getCode())) {
            builder.append(" and c.code ilike ? ");
            params.add(sqlHelper.escapeLikePattern(requestDTO.getCode()) + "%");
        }
        if (requestDTO.getIsEnabled() != null) {
            builder.append(" and c.is_enabled = ? ");
            params.add(requestDTO.getIsEnabled());
        }
        if (requestDTO.getIsPublic() != null) {
            builder.append(" and c.is_public = ? ");
            params.add(requestDTO.getIsPublic());
        }
        if (requestDTO.getCreatedAtFrom() != null) {
            builder.append(" and c.created_at >= ? ");
            params.add(requestDTO.getCreatedAtFrom());
        }
        if (requestDTO.getCreatedAtTo() != null) {
            builder.append(" and c.created_at < ? ");
            params.add(requestDTO.getCreatedAtTo());
        }
        if (!isCount) {
            builder.append(" order by c.code asc, c.id asc ");
            builder.append(" limit ? offset ? ");
            sqlHelper.setLimitOffset(params, result.getPageNumber(), result.getPageSize());
        }
        if (isCount) {
            Query query = entityManager.createNativeQuery(builder.toString());
            sqlHelper.setParams(query, params);
            long count = ConversionUtils.safeToLong(query.getSingleResult());
            result.setTotalItems(count);
            result.setTotalPages(sqlHelper.toPages(count, result.getPageSize()));
        } else {
            Query query = entityManager.createNativeQuery(builder.toString());
            sqlHelper.setParams(query, params);
            List<Object[]> objects = query.getResultList();
            List<ConfigurationDTO> items = objects.stream().map(x -> {
                ConfigurationDTO dto = new ConfigurationDTO();
                dto.setId(ConversionUtils.toUUID(x[0]));
                dto.setCode(ConversionUtils.toString(x[1]));
                dto.setValue(ConversionUtils.toString(x[2]));
                dto.setDescription(ConversionUtils.toString(x[3]));
                dto.setIsEnabled(ConversionUtils.toBoolean(x[4]));
                dto.setIsPublic(ConversionUtils.toBoolean(x[5]));
                dto.setCreatedAt(DateUtils.toInstant(x[6]));
                dto.setUpdatedAt(DateUtils.toInstant(x[7]));
                return dto;
            }).collect(Collectors.toCollection(ArrayList::new));
            result.setItems(items);
        }
        return result;
    }
}
