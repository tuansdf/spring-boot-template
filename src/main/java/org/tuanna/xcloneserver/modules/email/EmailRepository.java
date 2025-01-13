package org.tuanna.xcloneserver.modules.email;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tuanna.xcloneserver.entities.Email;

import java.util.UUID;

public interface EmailRepository extends JpaRepository<Email, UUID> {
}
