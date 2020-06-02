package cc.iteck.rm.controller.v1;

import cc.iteck.rm.model.account.RoleDto;
import cc.iteck.rm.model.account.UserDto;
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
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@Rollback
@Slf4j
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    void postRole(String roleCode) throws Exception {
        var roleDto = RoleDto.builder()
                .code(roleCode)
                .name("人员" + roleCode)
                .build();
        log.debug("roleDto: {}", roleDto);
        var objectMapper = new ObjectMapper();
        var json = objectMapper.writeValueAsString(roleDto);
        mockMvc.perform(post("/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(roleCode));
    }

    List<Map<String, Object>> getRoles() throws Exception {
        var jsonData = mockMvc.perform(get("/v1/roles"))
                .andDo(print())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return new ObjectMapper().readValue(jsonData, new TypeReference<>() {});
    }

    @Test
    List<Map<String, Object>> getUsers() throws Exception {
        var jsonData = mockMvc.perform(get("/v1/users"))
                .andDo(print())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return new ObjectMapper().readValue(jsonData, new TypeReference<>() {});
    }

    @Test
    void postUser() throws Exception {
        var userDto = UserDto.builder()
                .username("crazytravel")
                .password("abc123")
                .nickname("wangshuo")
                .email("user@12q.com")
                .build();
        var objectMapper = new ObjectMapper();
        var json = objectMapper.writeValueAsString(userDto);
        mockMvc.perform(post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("crazytravel"));
    }

    @Test
    void putUpdateUser() throws Exception {
        postUser();
        var users = getUsers();
        assert users != null && users.size() > 0;
        var originalUser = users.get(0);
        var userDto = UserDto.builder()
                .id(String.valueOf(originalUser.get("id")))
                .username("wangshuo")
                .build();
        var objectMapper = new ObjectMapper();
        var json = objectMapper.writeValueAsString(userDto);
        mockMvc.perform(patch("/v1/users/" + originalUser.get("id"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("wangshuo"));
    }

    @Test
    void given_id_when_found_user_then_return() throws Exception {
        postUser();
        var users = getUsers();
        assert users != null && users.size() > 0;
        var originalUser = users.get(0);
        mockMvc.perform(get("/v1/users/"+originalUser.get("id")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("crazytravel"));
    }

    @Test
    void given_id_when_found_user_then_delete() throws Exception {
        postUser();
        var users = getUsers();
        assert users != null && users.size() > 0;
        var originalUser = users.get(0);
        mockMvc.perform(delete("/v1/users/"+originalUser.get("id")))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void given_id_when_not_found_then_return_not_found_exception() throws Exception {
        postUser();
        mockMvc.perform(get("/v1/users/fakeId"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void given_repeat_data_when_create_then_throw_operate_exception() throws Exception {
        var userDto = UserDto.builder()
                .username("crazytravel")
                .password("abc123")
                .nickname("wangshuo")
                .email("user@12q.com")
                .build();
        var objectMapper = new ObjectMapper();
        var json = objectMapper.writeValueAsString(userDto);
        mockMvc.perform(post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("crazytravel"));
        mockMvc.perform(post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void test_assign_user_role() throws Exception {
        postUser();
        postRole("Admin");
        postRole("Normal");
        var users = getUsers();
        var roles = getRoles();
        assert users != null && users.size() > 0;
        var originalUser = users.get(0);
        var roleIds = roles.stream().map(role -> role.get("id")).collect(Collectors.toList());
        var objectMapper = new ObjectMapper();
        var json = objectMapper.writeValueAsString(roleIds);
        mockMvc.perform(put("/v1/users/" + originalUser.get("id") + "/assign-roles")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
                .andDo(print())
                .andExpect(status().isOk());
    }
}