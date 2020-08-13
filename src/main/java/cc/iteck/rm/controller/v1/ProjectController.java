package cc.iteck.rm.controller.v1;

import cc.iteck.rm.model.account.UserDto;
import cc.iteck.rm.model.project.ProjectDto;
import cc.iteck.rm.model.security.JwtUserDetails;
import cc.iteck.rm.model.stage.StageDto;
import cc.iteck.rm.service.ProjectService;
import cc.iteck.rm.service.ProjectUserService;
import cc.iteck.rm.service.StageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final StageService stageService;
    private final ProjectUserService projectUserService;

    public ProjectController(ProjectService projectService, StageService stageService, ProjectUserService projectUserService) {
        this.projectService = projectService;
        this.stageService = stageService;
        this.projectUserService = projectUserService;
    }

    @GetMapping
    public ResponseEntity<List<ProjectDto>> listProjects() {
        JwtUserDetails details = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ProjectDto> projects = projectService.findAllProjectsByCurrentUser(details.getUserId());
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/own")
    public ResponseEntity<List<ProjectDto>> listOwnProjects() {
        JwtUserDetails details = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ProjectDto> projects = projectService.findOwnProjects(details.getUserId());
        return ResponseEntity.ok(projects);
    }


    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectDto projectDto) {
        JwtUserDetails details = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ProjectDto project = projectService.createNewProject(projectDto, details.getUserId());
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
//        JwtUserDetails details = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<StageDto> stages = stageService.findSortedStagesByProjectId(id);
        return ResponseEntity.ok(stages);
    }

    @GetMapping("/active")
    public ResponseEntity<ProjectDto> getActiveProject() {
        JwtUserDetails details = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ProjectDto projectDto = projectService.findActiveProject(details.getUserId());
        return ResponseEntity.ok(projectDto);
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<ProjectDto> setActiveProject(@PathVariable String id) {
        ProjectDto project = projectService.activeProject(id);
        return ResponseEntity.ok(project);
    }

    @GetMapping("/{id}/users")
    public ResponseEntity<List<UserDto>> findProjectUsers(@PathVariable String id) {
        List<UserDto> users = projectUserService.findProjectUsers(id);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/{id}/users")
    public ResponseEntity<Boolean> assignUserToProject(@PathVariable String id, @RequestBody List<String> userIds) {
        Boolean status = projectUserService.addUserToProject(id, userIds);
        return ResponseEntity.ok(status);
    }

}
