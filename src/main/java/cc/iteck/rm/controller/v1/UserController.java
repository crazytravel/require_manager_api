package cc.iteck.rm.controller.v1;

import cc.iteck.rm.model.account.UserDto;
import cc.iteck.rm.model.account.UserRoleDto;
import cc.iteck.rm.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        var users = userService.findUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<UserDto> postUser(@RequestBody UserDto userDto) {
        var user = userService.createUser(userDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/" + userDto.getId()).build().toUri();
        return ResponseEntity.created(location).body(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> patchUser(@PathVariable String id, @RequestBody UserDto userDto) {
        var originalUserDto = userService.findUserById(id);
        BeanUtils.copyProperties(userDto, originalUserDto);
        var newUserDto = userService.updateUser(originalUserDto);
        return ResponseEntity.ok(newUserDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable String id) {
        UserDto userDto = userService.findUserById(id);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/{id}/assign-roles")
    public ResponseEntity<List<UserRoleDto>> assignRoles(@PathVariable String id, @RequestBody List<String> roleIds) {
        var userRoles = userService.assignRoles(id, roleIds);
        return ResponseEntity.ok(userRoles);
    }
}
