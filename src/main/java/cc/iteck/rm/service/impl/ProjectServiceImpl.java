package cc.iteck.rm.service.impl;

import cc.iteck.rm.exception.ResourceNotFoundException;
import cc.iteck.rm.exception.ResourceOperateFailedException;
import cc.iteck.rm.mapper.ProjectMapper;
import cc.iteck.rm.model.project.ProjectDto;
import cc.iteck.rm.model.project.ProjectEntity;
import cc.iteck.rm.service.ProjectService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectMapper projectMapper;

    public ProjectServiceImpl(ProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    @Override
    public List<ProjectDto> findAllProjects() {
        List<ProjectEntity> projects = projectMapper.selectList(Wrappers.emptyWrapper());
        return projects.stream().map(projectEntity -> {
            ProjectDto project = ProjectDto.builder().build();
            BeanUtils.copyProperties(projectEntity, project);
            return project;
        }).collect(Collectors.toList());
    }

    @Override
    public ProjectDto createNewProject(ProjectDto projectDto) {
        ProjectEntity projectEntity = ProjectEntity.builder().build();
        BeanUtils.copyProperties(projectDto, projectEntity);
        try {
            projectMapper.insert(projectEntity);
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
}
