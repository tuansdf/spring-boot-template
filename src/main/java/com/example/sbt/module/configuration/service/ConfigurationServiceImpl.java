package com.example.sbt.module.configuration.service;

import com.example.sbt.core.dto.PaginationData;
import com.example.sbt.core.exception.CustomException;
import com.example.sbt.core.helper.SQLHelper;
import com.example.sbt.core.mapper.CommonMapper;
import com.example.sbt.module.configuration.dto.ConfigurationDTO;
import com.example.sbt.module.configuration.dto.SearchConfigurationRequestDTO;
import com.example.sbt.module.configuration.entity.Configuration;
import com.example.sbt.module.configuration.entity.ConfigurationKV;
import com.example.sbt.module.configuration.repository.ConfigurationKVRepository;
import com.example.sbt.module.configuration.repository.ConfigurationRepository;
import com.example.sbt.shared.util.ConversionUtils;
import com.example.sbt.shared.util.DateUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class ConfigurationServiceImpl implements ConfigurationService {
    private static final int MAX_PUBLIC_CODES = 10;

    private final SQLHelper sqlHelper;
    private final CommonMapper commonMapper;
    private final EntityManager entityManager;
    private final ConfigurationRepository configurationRepository;
    private final ConfigurationKVRepository configurationKVRepository;
    private final ConfigurationValidator configurationValidator;

    @Override
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
        ConfigurationDTO resultDTO = commonMapper.toDTO(result);
        configurationKVRepository.save(commonMapper.toKV(resultDTO));
        return resultDTO;
    }

    @Override
    public ConfigurationDTO findOneById(UUID id) {
        if (id == null) return null;
        return configurationRepository.findById(id).map(commonMapper::toDTO).orElse(null);
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
    public ConfigurationKV findOneCachedByCode(String code) {
        if (StringUtils.isBlank(code)) return null;
        ConfigurationKV result = configurationKVRepository.findById(code).orElse(null);
        if (result == null) {
            result = commonMapper.toKV(findOneByCode(code));
            if (result == null) return null;
            configurationKVRepository.save(result);
        }
        return result;
    }

    @Override
    public String findValueByCode(String code) {
        ConfigurationKV result = findOneCachedByCode(code);
        if (result == null) {
            return null;
        }
        if (!ConversionUtils.safeToBoolean(result.getIsEnabled())) {
            return null;
        }
        return ConversionUtils.safeToString(result.getValue());
    }

    @Override
    public Map<String, String> findValuesByCodes(List<String> codes) {
        Map<String, String> result = new HashMap<>();
        if (CollectionUtils.isEmpty(codes)) return result;
        for (String code : codes) {
            result.put(code, findValueByCode(code));
        }
        return result;
    }

    @Override
    public String findPublicValueByCode(String code) {
        ConfigurationKV result = findOneCachedByCode(code);
        if (result == null) {
            return null;
        }
        if (!ConversionUtils.safeToBoolean(result.getIsEnabled()) || !ConversionUtils.safeToBoolean(result.getIsPublic())) {
            return null;
        }
        return ConversionUtils.safeToString(result.getValue());
    }

    @Override
    public Map<String, String> findPublicValuesByCodes(List<String> codes) {
        Map<String, String> result = new HashMap<>();
        if (CollectionUtils.isEmpty(codes)) return result;
        for (String code : codes.subList(0, MAX_PUBLIC_CODES)) {
            result.put(code, findPublicValueByCode(code));
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
                dto.setCreatedAt(DateUtils.toInstant(x[3]));
                dto.setUpdatedAt(DateUtils.toInstant(x[4]));
                return dto;
            }).collect(Collectors.toCollection(ArrayList::new));
            result.setItems(items);
        }
        return result;
    }
}
