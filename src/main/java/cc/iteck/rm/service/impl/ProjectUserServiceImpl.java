package cc.iteck.rm.service.impl;

import cc.iteck.rm.mapper.ProjectUserMapper;
import cc.iteck.rm.mapper.UserMapper;
import cc.iteck.rm.model.account.UserDto;
import cc.iteck.rm.model.account.UserEntity;
import cc.iteck.rm.model.project.ProjectUserEntity;
import cc.iteck.rm.service.ProjectUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectUserServiceImpl extends ServiceImpl<ProjectUserMapper, ProjectUserEntity> implements ProjectUserService {

    private final ProjectUserMapper projectUserMapper;
    private final UserMapper userMapper;

    public ProjectUserServiceImpl(ProjectUserMapper projectUserMapper, UserMapper userMapper) {
        this.projectUserMapper = projectUserMapper;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserDto> findProjectUsers(String projectId) {
        List<UserEntity> userEntities = userMapper.findUsersByProjectId(projectId);
        return userEntities.stream().map(userEntity -> {
            UserDto userDto = UserDto.builder().build();
            BeanUtils.copyProperties(userEntity, userDto);
            return userDto;
        }).collect(Collectors.toList());
    }

    @Override
    public Boolean addUserToProject(String projectId, List<String> userIds) {
        List<ProjectUserEntity> projectUserEntities = userIds.stream().map(userId -> ProjectUserEntity.builder()
                .projectId(projectId)
                .userId(userId)
                .build())
                .collect(Collectors.toList());
        return this.saveOrUpdateBatch(projectUserEntities);
    }
}
