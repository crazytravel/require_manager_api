package cc.iteck.rm.controller.v1;

import cc.iteck.rm.model.SucceedWrapper;
import cc.iteck.rm.model.task.OrderTaskForm;
import cc.iteck.rm.model.task.TaskDto;
import cc.iteck.rm.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    @GetMapping
    public ResponseEntity<List<TaskDto>> listTasks() {
        List<TaskDto> tasks = taskService.findAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskDto taskDto) {
        TaskDto task = taskService.createNewTask(taskDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/" + task.getId()).build().toUri();
        return ResponseEntity.created(location).body(task);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTask(@PathVariable String id) {
        TaskDto task = taskService.findTask(id);
        return ResponseEntity.ok(task);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable String id, @RequestBody TaskDto taskDto) {
        taskDto.setId(id);
        TaskDto task = taskService.updateTask(taskDto);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/reorder")
    public ResponseEntity<SucceedWrapper<TaskDto>> reorderTaskList(@PathVariable String id,
                                                          @RequestBody OrderTaskForm orderTaskForm) {
        TaskDto task = taskService.reorderTaskList(id, orderTaskForm);
        SucceedWrapper<TaskDto> success = SucceedWrapper.<TaskDto>builder().code(200).message("success").data(task).build();
        return ResponseEntity.ok(success);
    }
}
