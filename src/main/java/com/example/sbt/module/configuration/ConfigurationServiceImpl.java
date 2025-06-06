package com.example.sbt.module.configuration;

import com.example.sbt.common.constant.CommonStatus;
import com.example.sbt.common.constant.KVKey;
import com.example.sbt.common.constant.ResultSetName;
import com.example.sbt.common.dto.PaginationData;
import com.example.sbt.common.exception.CustomException;
import com.example.sbt.common.mapper.CommonMapper;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.common.util.SQLHelper;
import com.example.sbt.module.configuration.dto.ConfigurationDTO;
import com.example.sbt.module.configuration.dto.SearchConfigurationRequestDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private final StringRedisTemplate redisTemplate;

    private void setValueToKVByCode(String code, String value) {
        if (StringUtils.isBlank(code) || value == null) return;
        String kvKey = KVKey.CONFIGURATION_VALUE_BY_CODE.concat(code);
        redisTemplate.opsForValue().set(kvKey, value);
    }

    private String getValueFromKVByCode(String code) {
        if (StringUtils.isBlank(code)) return null;
        String kvKey = KVKey.CONFIGURATION_VALUE_BY_CODE.concat(code);
        return ConversionUtils.toString(redisTemplate.opsForValue().get(kvKey));
    }

    @Override
    public ConfigurationDTO save(ConfigurationDTO requestDTO) {
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
            String code = ConversionUtils.safeToString(requestDTO.getCode()).trim().toUpperCase();
            if (configurationRepository.existsByCode(code)) {
                throw new CustomException(HttpStatus.CONFLICT);
            }
            result = new Configuration();
            result.setCode(code);
        }
        result.setValue(requestDTO.getValue());
        result.setDescription(requestDTO.getDescription());
        result.setStatus(requestDTO.getStatus());
        result = configurationRepository.save(result);
        setValueToKVByCode(result.getCode(), result.getValue());
        return commonMapper.toDTO(result);
    }

    @Override
    public ConfigurationDTO findOneById(UUID id) {
        return commonMapper.toDTO(configurationRepository.findById(id).orElse(null));
    }

    @Override
    public ConfigurationDTO findOneByIdOrThrow(UUID id) {
        ConfigurationDTO result = findOneById(id);
        if (result == null) {
            throw new CustomException(HttpStatus.NOT_FOUND);
        }
        return result;
    }

    @Override
    public ConfigurationDTO findOneByCode(String code) {
        return commonMapper.toDTO(configurationRepository.findTopByCode(code).orElse(null));
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
        if (StringUtils.isBlank(code)) return null;
        String result = getValueFromKVByCode(code);
        if (result == null) {
            result = configurationRepository.findTopValueByCodeAndStatus(code, CommonStatus.ACTIVE);
        }
        if (result != null) {
            setValueToKVByCode(code, result);
        }
        return result;
    }

    @Override
    public PaginationData<ConfigurationDTO> search(SearchConfigurationRequestDTO requestDTO, boolean isCount) {
        PaginationData<ConfigurationDTO> result = executeSearch(requestDTO, true);
        if (!isCount && result.getTotalItems() > 0) {
            result.setItems(executeSearch(requestDTO, false).getItems());
        }
        return result;
    }

    private PaginationData<ConfigurationDTO> executeSearch(SearchConfigurationRequestDTO requestDTO, boolean isCount) {
        PaginationData<ConfigurationDTO> result = SQLHelper.initData(requestDTO.getPageNumber(), requestDTO.getPageSize());
        Map<String, Object> params = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        if (isCount) {
            builder.append(" select count(*) ");
        } else {
            builder.append(" select c.* ");
        }
        builder.append(" from configuration c ");
        builder.append(" where 1=1 ");
        if (StringUtils.isNotEmpty(requestDTO.getCode())) {
            builder.append(" and c.code = :code ");
            params.put("code", requestDTO.getCode());
        }
        if (StringUtils.isNotEmpty(requestDTO.getStatus())) {
            builder.append(" and c.status = :status ");
            params.put("status", requestDTO.getStatus());
        }
        if (requestDTO.getCreatedAtFrom() != null) {
            builder.append(" and c.created_at >= :createdAtFrom ");
            params.put("createdAtFrom", requestDTO.getCreatedAtFrom().truncatedTo(SQLHelper.MIN_TIME_PRECISION));
        }
        if (requestDTO.getCreatedAtTo() != null) {
            builder.append(" and c.created_at <= :createdAtTo ");
            params.put("createdAtTo", requestDTO.getCreatedAtTo().truncatedTo(SQLHelper.MIN_TIME_PRECISION));
        }
        if (!isCount) {
            builder.append(SQLHelper.toLimitOffset(result.getPageNumber(), result.getPageSize()));
        }
        if (isCount) {
            Query query = entityManager.createNativeQuery(builder.toString());
            SQLHelper.setParams(query, params);
            long count = ConversionUtils.safeToLong(query.getSingleResult());
            result.setTotalItems(count);
            result.setTotalPages(SQLHelper.toPages(count, result.getPageSize()));
        } else {
            Query query = entityManager.createNativeQuery(builder.toString(), ResultSetName.CONFIGURATION_SEARCH);
            SQLHelper.setParams(query, params);
            List<ConfigurationDTO> items = query.getResultList();
            result.setItems(items);
        }
        return result;
    }

}
