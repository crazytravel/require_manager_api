package cc.iteck.rm.service;

import cc.iteck.rm.model.account.PermissionDto;
import cc.iteck.rm.model.account.UserDto;
import cc.iteck.rm.model.account.UserRoleDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    List<UserDto> findUsers();

    UserDto findUserById(String id);

    UserDto findUserByUsername(String username);

    UserDto updateUser(UserDto userDto);

    void deleteUserById(String id);

    List<UserRoleDto> assignRoles(String userId, List<String> roleIds);

    List<UserRoleDto> listUserRoleByUser(String userId);

}
