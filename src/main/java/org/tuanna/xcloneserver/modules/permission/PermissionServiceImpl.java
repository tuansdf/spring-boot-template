package org.tuanna.xcloneserver.modules.permission;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.tuanna.xcloneserver.entities.Permission;
import org.tuanna.xcloneserver.mappers.CommonMapper;
import org.tuanna.xcloneserver.modules.permission.dtos.PermissionDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class PermissionServiceImpl implements PermissionService {

    private final CommonMapper commonMapper;
    private final PermissionRepository permissionRepository;

    @Override
    public List<String> findAllCodesByRoleId(Long roleId) {
        List<String> result = permissionRepository.findAllCodesByRoleId(roleId);
        if (CollectionUtils.isEmpty(result)) {
            return new ArrayList<>();
        }
        return result;
    }

    @Override
    public List<String> findAllCodesByUserId(UUID userId) {
        List<String> result = permissionRepository.findAllCodesByUserId(userId);
        if (CollectionUtils.isEmpty(result)) {
            return new ArrayList<>();
        }
        return result;
    }

    @Override
    public List<PermissionDTO> findAllByRoleId(Long roleId) {
        List<Permission> result = permissionRepository.findAllByRoleId(roleId);
        if (CollectionUtils.isEmpty(result)) {
            return new ArrayList<>();
        }
        return result.stream().map(commonMapper::toDTO).toList();
    }

    @Override
    public List<PermissionDTO> findAllByUserId(UUID userId) {
        List<Permission> result = permissionRepository.findAllByUserId(userId);
        if (CollectionUtils.isEmpty(result)) {
            return new ArrayList<>();
        }
        return result.stream().map(commonMapper::toDTO).toList();
    }

}
