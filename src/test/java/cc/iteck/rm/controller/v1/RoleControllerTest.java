package cc.iteck.rm.controller.v1;

import cc.iteck.rm.model.AbstractDto;
import cc.iteck.rm.model.account.PermissionDto;
import cc.iteck.rm.model.account.RoleDto;
import cc.iteck.rm.service.PermissionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Rollback
@Slf4j
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PermissionService permissionService;

    void createNewPermission(String permissionCode) {
        var permissionDto = PermissionDto.builder()
                .code(permissionCode)
                .name("权限" + permissionCode)
                .build();
        permissionDto = permissionService.createPermission(permissionDto);
        log.debug("permissionDto: {}", permissionDto);
    }


    @Test
    List<Map<String, Object>> getRoles() throws Exception {
        var jsonData = mockMvc.perform(get("/v1/roles"))
                .andDo(print())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return new ObjectMapper().readValue(jsonData, new TypeReference<>() {
        });
    }

    @Test
    void createNewRole() throws Exception {
        var role = RoleDto.builder()
                .code("ADMIN")
                .name("管理员")
                .description("admin user role")
                .build();
        var objectMapper = new ObjectMapper();
        var json = objectMapper.writeValueAsString(role);
        mockMvc.perform(post("/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("ADMIN"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }


    @Test
    void updateRole() throws Exception {
        createNewRole();
        var roles = getRoles();
        assert roles != null && roles.size() > 0;
        var originalUser = roles.get(0);
        var role = RoleDto.builder()
                .code("DEVELOPER")
                .name("开发")
                .build();
        var objectMapper = new ObjectMapper();
        var json = objectMapper.writeValueAsString(role);
        mockMvc.perform(patch("/v1/roles/" + originalUser.get("id"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DEVELOPER"));
    }

    @Test
    void given_id_when_found_then_return() throws Exception {
        createNewRole();
        var roles = getRoles();
        assert roles != null && roles.size() > 0;
        var originalUser = roles.get(0);
        mockMvc.perform(get("/v1/roles/" + originalUser.get("id")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("管理员"));
    }

    @Test
    void deleteRole() throws Exception {
        createNewRole();
        var roles = getRoles();
        assert roles != null && roles.size() > 0;
        var originalUser = roles.get(0);
        mockMvc.perform(delete("/v1/roles/" + originalUser.get("id")))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void test_when_role_not_found_then_throw_not_found_exception() throws Exception {
        createNewRole();
        mockMvc.perform(get("/v1/roles/fakeId"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void test_role_grant_permission() throws Exception {
        createNewRole();
        createNewPermission("READ");
        createNewPermission("WRITE");
        var permissions = permissionService.findPermissions();
        var roles = getRoles();
        assert roles != null && roles.size() > 0;
        var originalRole = roles.get(0);
        var permissionIds = permissions.stream().map(AbstractDto::getId).collect(Collectors.toList());
        var objectMapper = new ObjectMapper();
        var json = objectMapper.writeValueAsString(permissionIds);
        mockMvc.perform(put("/v1/roles/" + originalRole.get("id") + "/grant-permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk());
    }
}