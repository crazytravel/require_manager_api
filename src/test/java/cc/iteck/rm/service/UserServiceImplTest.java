package cc.iteck.rm.service;

import cc.iteck.rm.model.account.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Test
    void createUser() {
        UserDto userDto = UserDto.builder().build();
        userDto.setNickname("wangshuo");
        userDto.setUsername("crazytravel");
        userDto.setPassword("hello");
        UserDto user = userService.createUser(userDto);
        assert user.getCreatedAt() != null;
    }
}