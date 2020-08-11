package cc.iteck.rm.service.impl;

import cc.iteck.rm.mapper.ProjectUserMapper;
import cc.iteck.rm.model.account.UserDto;
import cc.iteck.rm.model.project.ProjectUserDto;
import cc.iteck.rm.model.project.ProjectUserEntity;
import cc.iteck.rm.service.ProjectUserService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
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
    public List<ProjectUserDto> findProjectUsers(String projectId) {
        List<ProjectUserEntity> projectUserEntities = projectUserMapper.selectList(Wrappers.<ProjectUserEntity>lambdaQuery()
                .eq(ProjectUserEntity::getProjectId, projectId));
        return projectUserEntities.stream().map(projectUserEntity -> {
            ProjectUserDto projectUserDto = ProjectUserDto.builder().build();
            BeanUtils.copyProperties(projectUserEntity, projectUserDto);
            return projectUserDto;
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
