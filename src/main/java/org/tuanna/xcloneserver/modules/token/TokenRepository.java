package org.tuanna.xcloneserver.modules.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.tuanna.xcloneserver.entities.Token;

import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, UUID> {

    @Modifying
    @Query(value = "update Token t set t.status = :status, t.updatedAt = :updatedAt, t.updatedBy = :updatedBy where t.ownerId = :ownerId and t.createdAt < :createdAt and t.type = :type and t.status <> :status")
    void updateStatusByOwnerIdAndTypeAndCreatedAtBefore(UUID ownerId, String type, OffsetDateTime createdAt, String status, OffsetDateTime updatedAt, UUID updatedBy);

}
