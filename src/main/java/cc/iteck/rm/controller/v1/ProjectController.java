package cc.iteck.rm.controller.v1;

import cc.iteck.rm.model.project.ProjectDto;
import cc.iteck.rm.model.stage.StageDto;
import cc.iteck.rm.service.ProjectService;
import cc.iteck.rm.service.StageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final StageService stageService;

    public ProjectController(ProjectService projectService, StageService stageService) {
        this.projectService = projectService;
        this.stageService = stageService;
    }

    @GetMapping
    public ResponseEntity<List<ProjectDto>> listProjects() {
        List<ProjectDto> projects = projectService.findAllProjects();
        return ResponseEntity.ok(projects);
    }

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectDto projectDto) {
        ProjectDto project = projectService.createNewProject(projectDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/" + project.getId()).build().toUri();
        return ResponseEntity.created(location).body(project);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProject(@PathVariable String id) {
        ProjectDto project = projectService.findProject(id);
        return ResponseEntity.ok(project);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectDto> updateProject(@RequestBody ProjectDto projectDto) {
        ProjectDto project = projectService.updateProject(projectDto);
        return ResponseEntity.ok(project);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable String id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/stages")
    public ResponseEntity<List<StageDto>> findSortedStagesByProjectId(@PathVariable String id) {
        List<StageDto> stages = stageService.findSortedStagesByProjectId(id);
        return ResponseEntity.ok(stages);
    }

    @GetMapping("/active")
    public ResponseEntity<ProjectDto> getActiveProject() {
        ProjectDto projectDto = projectService.findActiveProject();
        return ResponseEntity.ok(projectDto);
    }
}
