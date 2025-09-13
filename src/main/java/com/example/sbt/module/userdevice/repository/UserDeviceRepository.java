package com.example.sbt.module.userdevice.repository;

import com.example.sbt.module.userdevice.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, UUID> {
    @Query(value = "select fcm_token from user_device where user_id = :userId", nativeQuery = true)
    Set<String> findAllTokensByUserId(UUID userId);

    List<UserDevice> findAllByUserId(UUID userId);
}
