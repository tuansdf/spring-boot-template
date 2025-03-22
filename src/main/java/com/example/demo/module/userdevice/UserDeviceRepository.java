package com.example.demo.module.userdevice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, UUID> {

    @Query(value = "select fcm_token from user_device ud where ud.user_id = :userId", nativeQuery = true)
    List<String> findAllTokensByUserId(UUID userId);

    List<UserDevice> findAllByUserId(UUID userId);

}
