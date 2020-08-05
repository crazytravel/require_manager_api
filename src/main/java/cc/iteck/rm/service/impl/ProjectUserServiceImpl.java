package cc.iteck.rm.service.impl;

import cc.iteck.rm.mapper.ProjectUserMapper;
import cc.iteck.rm.model.account.UserDto;
import cc.iteck.rm.model.project.ProjectUserEntity;
import cc.iteck.rm.service.ProjectUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectUserServiceImpl extends ServiceImpl<ProjectUserMapper, ProjectUserEntity> implements ProjectUserService {

    private final ProjectUserMapper projectUserMapper;

    public ProjectUserServiceImpl(ProjectUserMapper projectUserMapper) {
        this.projectUserMapper = projectUserMapper;
    }

    @Override
    public List<UserDto> findProjectUsers(String projectId) {
        return null;
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
