package cc.iteck.rm.controller.v1;

import cc.iteck.rm.model.account.RoleDto;
import cc.iteck.rm.model.account.UserDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@Slf4j
@SpringBootTest
@WithMockUser(username = "user", password = "user")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class UserControllerTest {


    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
//                .apply(springSecurity())  // 这里配置Security认证
                .build();
    }

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
        return new ObjectMapper().readValue(jsonData, new TypeReference<>() {
        });
    }

    @Test
    @Order(2)
    List<Map<String, Object>> getUsers() throws Exception {
        var jsonData = mockMvc.perform(get("/v1/users"))
                .andDo(print())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return new ObjectMapper().readValue(jsonData, new TypeReference<>() {
        });
    }

    @Test
    @Order(1)
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
    @Order(2)
    void given_id_when_found_user_then_return() throws Exception {
        var users = getUsers();
        assert users != null && users.size() > 0;
        var originalUser = users.get(0);
        mockMvc.perform(get("/v1/users/" + originalUser.get("id")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("default"));
    }

    @Test
    @Order(2)
    void given_id_when_found_user_then_delete() throws Exception {
        var users = getUsers();
        assert users != null && users.size() > 0;
        var originalUser = users.get(1);
        mockMvc.perform(delete("/v1/users/" + originalUser.get("id")))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(2)
    void given_id_when_not_found_then_return_not_found_exception() throws Exception {
        mockMvc.perform(get("/v1/users/fakeId"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(2)
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
        postRole("Admin");
        postRole("Normal");
        var users = getUsers();
        var roles = getRoles();
        assert users != null && users.size() > 0;
        var originalUser = users.get(1);
        var roleIds = roles.stream().map(role -> role.get("id")).collect(Collectors.toList());
        var objectMapper = new ObjectMapper();
        var json = objectMapper.writeValueAsString(roleIds);
        mockMvc.perform(put("/v1/users/" + originalUser.get("id") + "/assign-roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    void putUpdateUser() throws Exception {
        var users = getUsers();
        assert users != null && users.size() > 0;
        var originalUser = users.get(1);
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
}