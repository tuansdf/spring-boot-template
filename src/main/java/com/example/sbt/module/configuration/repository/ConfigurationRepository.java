package com.example.sbt.module.configuration.repository;

import com.example.sbt.module.configuration.entity.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, UUID> {
    boolean existsByCode(String code);

    Optional<Configuration> findTopByCode(String code);

    List<Configuration> findAllByCodeIn(List<String> codes);
}
