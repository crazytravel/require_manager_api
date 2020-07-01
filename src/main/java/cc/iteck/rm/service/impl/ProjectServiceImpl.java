package cc.iteck.rm.service.impl;

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
        projectMapper.insert(projectEntity);
        BeanUtils.copyProperties(projectEntity, projectDto);
        return projectDto;
    }

    @Override
    public ProjectDto findProject(String id) {
        ProjectEntity projectEntity = projectMapper.selectById(id);
        ProjectDto projectDto = ProjectDto.builder().build();
        BeanUtils.copyProperties(projectEntity, projectDto);
        return projectDto;
    }

    @Override
    public ProjectDto updateProject(ProjectDto projectDto) {
        ProjectEntity projectEntity = projectMapper.selectById(projectDto.getId());
        BeanUtils.copyProperties(projectDto, projectEntity);
        projectMapper.updateById(projectEntity);
        BeanUtils.copyProperties(projectEntity, projectDto);
        return projectDto;
    }

    @Override
    public void deleteProject(String id) {
        projectMapper.deleteById(id);
    }
}
