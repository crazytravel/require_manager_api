package cc.iteck.rm.mapper;

import cc.iteck.rm.model.account.PermissionEntity;
import cc.iteck.rm.model.account.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper extends BaseMapper<UserEntity> {

    List<PermissionEntity> findUserPermissionsByUsername(@Param("username") String username);

    List<UserEntity> findUsersByProjectId(@Param("projectId") String projectId);
}
