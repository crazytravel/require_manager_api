package cc.iteck.rm.service.impl;

import cc.iteck.rm.exception.ResourceNotFoundException;
import cc.iteck.rm.exception.ResourceOperateFailedException;
import cc.iteck.rm.mapper.ProjectMapper;
import cc.iteck.rm.mapper.ProjectUserMapper;
import cc.iteck.rm.model.project.ProjectDto;
import cc.iteck.rm.model.project.ProjectEntity;
import cc.iteck.rm.model.project.ProjectUserEntity;
import cc.iteck.rm.model.security.JwtUserDetails;
import cc.iteck.rm.service.ProjectService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectMapper projectMapper;
    private final ProjectUserMapper projectUserMapper;

    public ProjectServiceImpl(ProjectMapper projectMapper, ProjectUserMapper projectUserMapper) {
        this.projectMapper = projectMapper;
        this.projectUserMapper = projectUserMapper;
    }

    @Override
    public List<ProjectDto> findAllProjectsByCurrentUser(String userId) {
        List<ProjectEntity> projects = projectMapper.findProjectsWithUserId(userId, null);
        return projects.stream().map(projectEntity -> {
            ProjectDto project = ProjectDto.builder().build();
            BeanUtils.copyProperties(projectEntity, project);
            return project;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDto> findOwnProjects(String userId) {
        List<ProjectEntity> projects = projectMapper.findProjectsWithUserId(userId, true);
        return projects.stream().map(projectEntity -> {
            ProjectDto project = ProjectDto.builder().build();
            BeanUtils.copyProperties(projectEntity, project);
            return project;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProjectDto createNewProject(ProjectDto projectDto) {
        ProjectEntity projectEntity = ProjectEntity.builder().build();
        BeanUtils.copyProperties(projectDto, projectEntity);
        JwtUserDetails details = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            projectMapper.insert(projectEntity);
            ProjectUserEntity projectUserEntity = ProjectUserEntity.builder()
                    .projectId(projectEntity.getId())
                    .userId(details.getUserId()).build();
            projectUserMapper.insert(projectUserEntity);
        } catch (Exception e) {
            throw new ResourceOperateFailedException("create project failed, error: ", e);
        }
        BeanUtils.copyProperties(projectEntity, projectDto);
        return projectDto;
    }

    @Override
    public ProjectDto findProject(String id) {
        ProjectEntity projectEntity = projectMapper.selectById(id);
        if (projectEntity == null) {
            throw new ResourceNotFoundException("cannot found project by id: " + id);
        }
        ProjectDto projectDto = ProjectDto.builder().build();
        BeanUtils.copyProperties(projectEntity, projectDto);
        return projectDto;
    }

    @Override
    public ProjectDto updateProject(ProjectDto projectDto) {
        ProjectEntity projectEntity = projectMapper.selectById(projectDto.getId());
        if (projectEntity == null) {
            throw new ResourceNotFoundException("cannot found project by id: " + projectDto.getId());
        }
        BeanUtils.copyProperties(projectDto, projectEntity);
        try {
            projectMapper.updateById(projectEntity);
        } catch (Exception e) {
            throw new ResourceOperateFailedException("create project failed, error: ", e);
        }
        BeanUtils.copyProperties(projectEntity, projectDto);
        return projectDto;
    }

    @Override
    public void deleteProject(String id) {
        try {
            projectMapper.deleteById(id);
        } catch (Exception e) {
            throw new ResourceOperateFailedException("delete project failed by id: " + id, e);
        }
    }

    @Override
    public ProjectDto findActiveProject() {
        ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery()
                .eq(ProjectEntity::getActive, true));
        ProjectDto projectDto = ProjectDto.builder().build();
        BeanUtils.copyProperties(projectEntity, projectDto);
        return projectDto;
    }

    @Transactional
    @Override
    public ProjectDto activeProject(String id) {
        ProjectEntity updateValue = ProjectEntity.builder().active(false).build();
        projectMapper.update(updateValue, Wrappers.<ProjectEntity>lambdaUpdate().eq(ProjectEntity::getActive, true));
        ProjectEntity projectEntity = projectMapper.selectById(id);
        projectEntity.setActive(true);
        projectMapper.updateById(projectEntity);
        return findProject(id);
    }
}
