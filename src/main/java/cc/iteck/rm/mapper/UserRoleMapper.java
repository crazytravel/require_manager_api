package cc.iteck.rm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cc.iteck.rm.model.account.UserRoleEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserRoleMapper extends BaseMapper<UserRoleEntity> {

    void batchInsert(@Param("userId")String userId, @Param("roleIds")List<String> roleIds);
}
