package cc.iteck.rm.service;

import cc.iteck.rm.model.account.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Test
    void createUser() {
        UserDto userDto = UserDto.builder().build();
        userDto.setNickname("wangshuo");
        userDto.setUsername("crazytravel");
        userDto.setPassword("admin");
        UserDto user = userService.createUser(userDto);
        log.debug("password: {}", userDto.getPassword());
        var match = PasswordEncoderFactories.createDelegatingPasswordEncoder().matches("admin", user.getPassword());
        assert match;
        assert user.getCreatedAt() != null;
    }
}