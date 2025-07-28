package com.example.sbt.module.configuration.repository;

import com.example.sbt.module.configuration.entity.ConfigurationKV;
import org.springframework.data.repository.CrudRepository;

public interface ConfigurationKVRepository extends CrudRepository<ConfigurationKV, String> {
}
