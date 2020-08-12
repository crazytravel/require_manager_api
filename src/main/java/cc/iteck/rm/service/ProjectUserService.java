package cc.iteck.rm.service;

import cc.iteck.rm.model.account.UserDto;
import cc.iteck.rm.model.project.ProjectUserDto;
import cc.iteck.rm.model.project.ProjectUserEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ProjectUserService extends IService<ProjectUserEntity> {

    List<UserDto> findProjectUsers(String projectId);

    Boolean addUserToProject(String projectId, List<String> userIds);
}
