package com.example.sbt.module.configuration.repository;

import com.example.sbt.module.configuration.dto.ConfigurationDTO;
import org.springframework.data.repository.CrudRepository;

public interface ConfigurationKVRepository extends CrudRepository<ConfigurationDTO, String> {
}
