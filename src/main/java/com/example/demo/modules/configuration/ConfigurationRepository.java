package com.example.demo.modules.configuration;

import com.example.demo.entities.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {

    boolean existsByCode(String code);

    Optional<Configuration> findTopByCode(String code);

    @Query(value = "select c.value from configuration c where c.code = :code and c.status = :status limit 1", nativeQuery = true)
    String findTopValueByCodeAndStatus(String code, String status);

}
