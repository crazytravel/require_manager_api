package cc.iteck.rm.service;

import cc.iteck.rm.model.account.PermissionDto;

import java.util.List;

public interface PermissionService {

    PermissionDto createPermission(PermissionDto permissionDto);

    List<PermissionDto> findPermissions();
}
