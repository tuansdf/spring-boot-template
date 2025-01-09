package org.tuanna.xcloneserver.modules.configuration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tuanna.xcloneserver.entities.Configuration;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {
}
