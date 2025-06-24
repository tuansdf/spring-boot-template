package com.example.sbt.module.remoteconfig;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface RemoteConfigRepository extends JpaRepository<RemoteConfig, UUID> {

    boolean existsByCode(String code);

    Optional<RemoteConfig> findTopByCode(String code);

    @Query(value = "select value from remote_config where code = :code and status = :status order by id asc limit 1", nativeQuery = true)
    String findTopValueByCodeAndStatus(String code, String status);

    List<RemoteConfig> findAllByCodeInAndStatusAndIsPublic(Set<String> codes, String status, Boolean isPublic);

}
