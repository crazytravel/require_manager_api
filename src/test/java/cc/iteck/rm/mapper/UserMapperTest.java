package cc.iteck.rm.mapper;

import cc.iteck.rm.model.account.UserEntity;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@Rollback
@Transactional
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @BeforeEach
    public void test1Insert() {
        var user = UserEntity.builder().build();
        user.setUsername("wangshuo");
        user.setPassword("abc124");
        user.setNickname("crazytravel");
        var count = userMapper.insert(user);
        assert count > 0;
    }

    @Test
    void test3Select() {
        var user = userMapper.selectOne(Wrappers.<UserEntity>lambdaQuery().eq(UserEntity::getUsername, "wangshuo"));
        assert user != null;
        log.debug("userEntity: {}", user);
    }

    @Test
    public void test2Update() {
        var user = userMapper.selectOne(Wrappers.<UserEntity>lambdaQuery().eq(UserEntity::getUsername, "wangshuo"));
        user.setNickname("Hello");
        userMapper.updateById(user);
        log.debug("修改后的用户:{}", user);
        assert "Hello".equals(user.getNickname());
    }

    @Test
    void test4Delete() {
        var count = userMapper.delete(Wrappers.<UserEntity>lambdaQuery().eq(UserEntity::getUsername, "wangshuo"));
        assert count > 0;
    }
}
