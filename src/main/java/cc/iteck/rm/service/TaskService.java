package cc.iteck.rm.service;

import cc.iteck.rm.model.task.TaskDto;

import java.util.List;

public interface TaskService {
    List<TaskDto> findAllTasks();

    TaskDto createNewTask(TaskDto taskDto);

    TaskDto findTask(String id);

    TaskDto updateTask(TaskDto taskDto);

    void deleteTask(String id);
}
