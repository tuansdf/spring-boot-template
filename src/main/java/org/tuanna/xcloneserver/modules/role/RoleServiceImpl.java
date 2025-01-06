package org.tuanna.xcloneserver.modules.role;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.tuanna.xcloneserver.entities.Role;
import org.tuanna.xcloneserver.mappers.CommonMapper;
import org.tuanna.xcloneserver.modules.role.dtos.RoleDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    private final CommonMapper commonMapper;
    private final RoleRepository roleRepository;

    @Override
    public List<String> findAllCodesByUserId(UUID userId) {
        List<String> result = roleRepository.findAllCodesByUserId(userId);
        if (CollectionUtils.isEmpty(result)) {
            return new ArrayList<>();
        }
        return result;
    }

    @Override
    public List<RoleDTO> findAllByUserId(UUID userId) {
        List<Role> result = roleRepository.findAllByUserId(userId);
        if (CollectionUtils.isEmpty(result)) {
            return new ArrayList<>();
        }
        return result.stream().map(commonMapper::toDTO).toList();
    }

}
