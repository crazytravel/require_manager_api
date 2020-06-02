package cc.iteck.rm.controller.v1;

import cc.iteck.rm.model.account.RoleDto;
import cc.iteck.rm.model.account.RolePermissionDto;
import cc.iteck.rm.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<RoleDto> createNewRole(@RequestBody RoleDto roleDto) {
        var newRole = roleService.createNewRole(roleDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/" + newRole.getId()).build().toUri();
        return ResponseEntity.created(location).body(newRole);
    }

    @GetMapping
    public ResponseEntity<List<RoleDto>> getRoles() {
        var roles = roleService.listRoles();
        return ResponseEntity.ok(roles);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RoleDto> updateRole(@PathVariable String id,
                                              @RequestBody
                                              @NotNull RoleDto roleDto) {
        roleDto.setId(id);
        var role = roleService.updateRole(roleDto);
        return ResponseEntity.ok(role);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDto> getRole(@PathVariable String id) {
        var role = roleService.findRole(id);
        return ResponseEntity.ok(role);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable String id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}/grant-permissions")
    public ResponseEntity<List<RolePermissionDto>> grantPermissions(@PathVariable String id, @RequestBody List<String> roleIds) {
        var rolePermissions = roleService.grantPermissions(id, roleIds);
        return ResponseEntity.ok(rolePermissions);
    }
}
