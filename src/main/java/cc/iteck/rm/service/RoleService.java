package cc.iteck.rm.service;

import cc.iteck.rm.model.account.RoleDto;
import cc.iteck.rm.model.account.RolePermissionDto;

import java.util.List;

public interface RoleService {

    RoleDto createNewRole(RoleDto roleDto);

    RoleDto updateRole(RoleDto roleDto);

    void deleteRole(String id);

    RoleDto getRole(String id);

    List<RoleDto> listRoles();

    List<RolePermissionDto> grantPermissions(String roleId, List<String> permissionIds);

    List<RolePermissionDto> findRolePermissionByRoleId(String roleId);

}
