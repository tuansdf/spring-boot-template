package com.example.sbt.module.configuration;

import com.example.sbt.common.constant.CommonStatus;
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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class ConfigurationServiceImpl implements ConfigurationService {

    private static final int MAX_PUBLIC_CODES = 10;
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
        result.setStatus(requestDTO.getStatus());
        result.setIsPublic(ConversionUtils.safeToBoolean(requestDTO.getIsPublic()));
        result = configurationRepository.save(result);
        ConfigurationDTO resultDTO = commonMapper.toDTO(result);
        configurationKVRepository.save(resultDTO);
        return resultDTO;
    }

    @Override
    public ConfigurationDTO findOneByCode(String code) {
        if (StringUtils.isBlank(code)) return null;
        ConfigurationDTO result = configurationKVRepository.findById(code).orElse(null);
        if (result != null) {
            return result;
        }
        result = commonMapper.toDTO(configurationRepository.findTopByCode(code).orElse(null));
        if (result != null) {
            configurationKVRepository.save(result);
        }
        return result;
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
        if (StringUtils.isBlank(code)) {
            return null;
        }
        ConfigurationDTO result = findOneByCode(code);
        if (result == null || !CommonStatus.ACTIVE.equals(result.getStatus())) {
            return null;
        }
        return ConversionUtils.safeToString(result.getValue());
    }

    @Override
    public Map<String, String> findPublicValues(List<String> codes) {
        if (CollectionUtils.isEmpty(codes) || codes.size() > MAX_PUBLIC_CODES) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        for (String code : codes) {
            if (StringUtils.isBlank(code)) {
                continue;
            }
            ConfigurationDTO configurationDTO = findOneByCode(code);
            if (configurationDTO == null) {
                continue;
            }
            if (!ConversionUtils.safeToBoolean(configurationDTO.getIsPublic())) {
                continue;
            }
            result.put(code, ConversionUtils.safeToString(configurationDTO.getValue()));
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
        if (StringUtils.isNotBlank(requestDTO.getCode())) {
            builder.append(" and c.code = :code ");
            params.put("code", requestDTO.getCode().trim());
        }
        if (StringUtils.isNotBlank(requestDTO.getStatus())) {
            builder.append(" and c.status = :status ");
            params.put("status", requestDTO.getStatus().trim());
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
            builder.append(" order by c.code asc, c.id asc ");
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
