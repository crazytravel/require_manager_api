package cc.iteck.rm.service.impl;

import cc.iteck.rm.exception.ResourceNotFoundException;
import cc.iteck.rm.exception.ResourceOperateFailedException;
import cc.iteck.rm.model.account.RoleDto;
import cc.iteck.rm.model.account.RoleEntity;
import cc.iteck.rm.model.account.RolePermissionDto;
import cc.iteck.rm.model.account.RolePermissionEntity;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cc.iteck.rm.mapper.RoleMapper;
import cc.iteck.rm.mapper.RolePermissionMapper;
import cc.iteck.rm.model.account.*;
import cc.iteck.rm.service.RoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;

    public RoleServiceImpl(RoleMapper roleMapper, RolePermissionMapper rolePermissionMapper) {
        this.roleMapper = roleMapper;
        this.rolePermissionMapper = rolePermissionMapper;
    }

    @Override
    public RoleDto createNewRole(RoleDto roleDto) {
        var roleEntity = RoleEntity.builder().build();
        BeanUtils.copyProperties(roleDto, roleEntity);
        try {
            roleMapper.insert(roleEntity);
        } catch (Exception e) {
            throw new ResourceOperateFailedException("create new role failed", e);
        }
        BeanUtils.copyProperties(roleEntity, roleDto);
        return roleDto;
    }

    @Override
    public RoleDto updateRole(RoleDto roleDto) {
        var roleEntity = roleMapper.selectById(roleDto.getId());
        BeanUtils.copyProperties(roleDto, roleEntity);
        try {
            roleMapper.updateById(roleEntity);
        } catch (Exception e) {
            throw new ResourceOperateFailedException("update role failed", e);
        }
        BeanUtils.copyProperties(roleEntity, roleDto);
        return roleDto;
    }

    @Override
    public void deleteRole(String id) {
        try {
            roleMapper.deleteById(id);
        } catch (Exception e) {
            throw new ResourceOperateFailedException("delete role failed by id: " + id, e);
        }
    }

    @Override
    public RoleDto getRole(String id) {
        var roleDto = RoleDto.builder().build();
        var roleEntity = roleMapper.selectById(id);
        if (roleEntity == null) {
            throw new ResourceNotFoundException("cannot found role by id: " + id);
        }
        BeanUtils.copyProperties(roleEntity, roleDto);
        return roleDto;
    }

    @Override
    public List<RoleDto> listRoles() {
        var roles = roleMapper.selectList(Wrappers.emptyWrapper());
        return roles.stream().map(roleEntity -> {
            var roleDto = RoleDto.builder().build();
            BeanUtils.copyProperties(roleEntity, roleDto);
            return roleDto;
        }).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<RolePermissionDto> grantPermissions(String roleId, List<String> permissionIds) {
        try {
            rolePermissionMapper.delete(Wrappers.<RolePermissionEntity>lambdaUpdate()
                    .eq(RolePermissionEntity::getRoleId, roleId));
            for (var permissionId : permissionIds) {
                var rolePermission = RolePermissionEntity.builder()
                        .roleId(roleId)
                        .permissionId(permissionId)
                        .build();
                rolePermissionMapper.insert(rolePermission);
            }
        } catch (Exception e) {
            throw new ResourceOperateFailedException("assign permission failed to role id: " + roleId, e);
        }
        return findRolePermissionByRoleId(roleId);
    }

    @Override
    public List<RolePermissionDto> findRolePermissionByRoleId(String roleId) {
        var rolePermissionEntities = rolePermissionMapper.selectList(Wrappers.<RolePermissionEntity>lambdaQuery()
                .eq(RolePermissionEntity::getRoleId, roleId));
        return rolePermissionEntities.stream().map(rolePermissionEntity -> {
            var rolePermissionDto = RolePermissionDto.builder().build();
            BeanUtils.copyProperties(rolePermissionEntity, rolePermissionDto);
            return rolePermissionDto;
        }).collect(Collectors.toList());
    }
}
