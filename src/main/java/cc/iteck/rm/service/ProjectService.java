package cc.iteck.rm.service;

import cc.iteck.rm.model.project.ProjectDto;

import java.util.List;

public interface ProjectService {
    List<ProjectDto> findAllProjectsByCurrentUser(String userId);

    ProjectDto createNewProject(ProjectDto projectDto);

    ProjectDto findProject(String id);

    ProjectDto updateProject(ProjectDto projectDto);

    void deleteProject(String id);

    ProjectDto findActiveProject();

    ProjectDto activeProject(String id);
}
