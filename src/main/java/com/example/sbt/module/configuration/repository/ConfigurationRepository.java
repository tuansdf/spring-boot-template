package com.example.sbt.module.configuration.repository;

import com.example.sbt.module.configuration.entity.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, UUID> {

    boolean existsByCode(String code);

    Optional<Configuration> findTopByCode(String code);

    @Query(value = "select value from configuration where code = :code and status = :status order by id asc limit 1", nativeQuery = true)
    String findTopValueByCodeAndStatus(String code, String status);

    List<Configuration> findAllByCodeInAndStatusAndIsPublic(List<String> codes, String status, Boolean isPublic);

}
