package org.tuanna.xcloneserver.modules.token;

import java.util.UUID;

public interface TokenService {

    boolean validateTokenById(UUID id, String type);

}
