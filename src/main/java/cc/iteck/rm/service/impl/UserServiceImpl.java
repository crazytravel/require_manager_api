package cc.iteck.rm.service.impl;

import cc.iteck.rm.exception.AuthenticationException;
import cc.iteck.rm.exception.ResourceNotFoundException;
import cc.iteck.rm.exception.ResourceOperateFailedException;
import cc.iteck.rm.mapper.UserMapper;
import cc.iteck.rm.mapper.UserRoleMapper;
import cc.iteck.rm.model.account.*;
import cc.iteck.rm.service.UserService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;

    public UserServiceImpl(UserMapper userMapper, UserRoleMapper userRoleMapper) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        var userEntity = UserEntity.builder().build();
        BeanUtils.copyProperties(userDto, userEntity);

        var password = PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(userDto.getPassword());
        userEntity.setPassword(password);
        try {
            userMapper.insert(userEntity);
        } catch (Exception e) {
            throw new ResourceOperateFailedException("create user failed, error: ", e);
        }
        BeanUtils.copyProperties(userEntity, userDto);
        return userDto;
    }

    @Override
    public List<UserDto> findUsers() {
        var users = userMapper.selectList(Wrappers.emptyWrapper());
        return users.stream().map(userEntity -> {
            UserDto userDto = UserDto.builder().build();
            BeanUtils.copyProperties(userEntity, userDto);
            return userDto;
        }).collect(Collectors.toList());
    }

    @Override
    public UserDto findUserById(String id) {
        UserDto userDto = UserDto.builder().build();
        var userEntity = userMapper.selectById(id);
        if (userEntity == null) {
            return null;
        }
        BeanUtils.copyProperties(userEntity, userDto);
        return userDto;
    }

    @Override
    public UserDto findUserWithPermissionsByUsername(String username) {
        var userDto = findUserByUsername(username);
        if (userDto == null) {
            return null;
        }
        var permissions = userMapper.findUserPermissionsByUsername(username);
        var permissionDtos = permissions.stream().map(permission -> {
            var permissionDto = PermissionDto.builder().build();
            BeanUtils.copyProperties(permission, permissionDto);
            return permissionDto;
        }).collect(Collectors.toList());
        userDto.setPermissions(permissionDtos);
        return userDto;
    }

    @Override
    public UserDto findUserByUsername(String username) {
        var userEntity = userMapper.selectOne(Wrappers.<UserEntity>lambdaQuery().eq(UserEntity::getUsername, username));
        if (userEntity == null) {
            return null;
        }
        var userDto = UserDto.builder().build();
        BeanUtils.copyProperties(userEntity, userDto);
        return userDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        UserEntity userEntity = userMapper.selectById(userDto.getId());
        BeanUtils.copyProperties(userDto, userEntity);
        try {
            userMapper.updateById(userEntity);
        } catch (Exception e) {
            throw new ResourceOperateFailedException("update user failed by id: " + userDto.getId(), e);
        }
        BeanUtils.copyProperties(userEntity, userDto);
        return userDto;
    }

    @Override
    public void deleteUserById(String id) {
        try {
            userMapper.deleteById(id);
        } catch (Exception e) {
            throw new ResourceOperateFailedException("delete user failed by id: " + id, e);
        }
    }

    @Transactional
    @Override
    public List<UserRoleDto> assignRoles(String userId, List<String> roleIds) {
        try {
            userRoleMapper.delete(Wrappers.<UserRoleEntity>lambdaUpdate().eq(UserRoleEntity::getUserId, userId));
            for (var roleId : roleIds) {
                var userRole = UserRoleEntity.builder()
                        .userId(userId)
                        .roleId(roleId)
                        .build();
                userRoleMapper.insert(userRole);
            }
        } catch (Exception e) {
            throw new ResourceOperateFailedException("assign role failed to user id: " + userId, e);
        }
        return listUserRoleByUser(userId);
    }

    @Override
    public List<UserRoleDto> listUserRoleByUser(String userId) {
        var userRoleList = userRoleMapper.selectList(Wrappers.<UserRoleEntity>lambdaQuery().eq(UserRoleEntity::getUserId, userId));
        return userRoleList.stream().map(userRoleEntity -> {
            var userRoleDto = UserRoleDto.builder().build();
            BeanUtils.copyProperties(userRoleEntity, userRoleDto);
            return userRoleDto;
        }).collect(Collectors.toList());
    }

    @Override
    public UserDto findUsernameAndPassword(String username, String password) {
        var userDto = findUserWithPermissionsByUsername(username);
        if (userDto == null) {
            throw new AuthenticationException("cannot found user with the username: " + username);
        }
        var valid = PasswordEncoderFactories.createDelegatingPasswordEncoder().matches(password, userDto.getPassword());
        if (!valid) {
            throw new AuthenticationException("auth failed, please input the correct password");
        }
        return userDto;
    }
}


