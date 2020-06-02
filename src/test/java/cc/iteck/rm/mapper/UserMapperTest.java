package cc.iteck.rm.mapper;

import cc.iteck.rm.model.account.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Rollback
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;


    UserEntity prepareData() {
        var user = UserEntity.builder().build();
        user.setUsername("wangshuo");
        user.setPassword("abc124");
        user.setNickname("crazytravel");
        return user;
    }

    @Test
    public void test1Insert() {
        var user = prepareData();
        var count = userMapper.insert(user);
        assert count > 0;
    }

    @Test
    public void test2Update() {
        var user = prepareData();
        userMapper.insert(user);
        user.setNickname("Hello");
        userMapper.updateById(user);
        log.debug("修改后的用户:{}", user);
        assert "Hello".equals(user.getNickname());
    }

    @Test
    void test3Select() {
        var user = prepareData();
        userMapper.insert(user);
        UserEntity userEntity = userMapper.selectById(user.getId());
        assert userEntity != null;
        log.debug("userEntity: {}", userEntity);
    }

    @Test
    void test4Delete() {
        var user = prepareData();
        userMapper.insert(user);
        int count = userMapper.deleteById(user.getId());
        assert count > 0;
    }
}
