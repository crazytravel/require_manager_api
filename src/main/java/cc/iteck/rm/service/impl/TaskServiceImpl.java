package cc.iteck.rm.service.impl;

import cc.iteck.rm.mapper.TaskMapper;
import cc.iteck.rm.model.task.TaskDto;
import cc.iteck.rm.model.task.TaskEntity;
import cc.iteck.rm.service.TaskService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskMapper taskMapper;

    public TaskServiceImpl(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }


    @Override
    public List<TaskDto> findAllTasks() {
        List<TaskEntity> tasks = taskMapper.selectList(Wrappers.emptyWrapper());
        return tasks.stream().map(taskEntity -> {
            TaskDto task = TaskDto.builder().build();
            BeanUtils.copyProperties(taskEntity, task);
            return task;
        }).collect(Collectors.toList());
    }

    @Override
    public TaskDto createNewTask(TaskDto taskDto) {
        TaskEntity taskEntity = TaskEntity.builder().build();
        BeanUtils.copyProperties(taskDto, taskEntity);
        taskMapper.insert(taskEntity);
        BeanUtils.copyProperties(taskEntity, taskDto);
        return taskDto;
    }

    @Override
    public TaskDto findTask(String id) {
        TaskEntity taskEntity = taskMapper.selectById(id);
        TaskDto taskDto = TaskDto.builder().build();
        BeanUtils.copyProperties(taskEntity, taskDto);
        return taskDto;
    }

    @Override
    public TaskDto updateTask(TaskDto taskDto) {
        TaskEntity taskEntity = taskMapper.selectById(taskDto.getId());
        BeanUtils.copyProperties(taskDto, taskEntity);
        taskMapper.updateById(taskEntity);
        BeanUtils.copyProperties(taskEntity, taskDto);
        return taskDto;
    }

    @Override
    public void deleteTask(String id) {
        taskMapper.deleteById(id);
    }
}
