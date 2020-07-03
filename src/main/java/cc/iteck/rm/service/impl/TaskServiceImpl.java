package cc.iteck.rm.service.impl;

import cc.iteck.rm.exception.ResourceNotFoundException;
import cc.iteck.rm.exception.ResourceOperateFailedException;
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
        try {
            taskMapper.insert(taskEntity);
        } catch (Exception e) {
            throw new ResourceOperateFailedException("create task failed, error: ", e);
        }
        BeanUtils.copyProperties(taskEntity, taskDto);
        return taskDto;
    }

    @Override
    public TaskDto findTask(String id) {
        TaskEntity taskEntity = taskMapper.selectById(id);
        if (taskEntity == null) {
            throw new ResourceNotFoundException("cannot found task by id: " + id);
        }
        TaskDto taskDto = TaskDto.builder().build();
        BeanUtils.copyProperties(taskEntity, taskDto);
        return taskDto;
    }

    @Override
    public TaskDto updateTask(TaskDto taskDto) {
        TaskEntity taskEntity = taskMapper.selectById(taskDto.getId());
        if (taskEntity == null) {
            throw new ResourceNotFoundException("cannot found task by id: " + taskDto.getId());
        }
        BeanUtils.copyProperties(taskDto, taskEntity);
        try {
            taskMapper.updateById(taskEntity);
        } catch (Exception e) {
            throw new ResourceOperateFailedException("create task failed, error: ", e);
        }
        BeanUtils.copyProperties(taskEntity, taskDto);
        return taskDto;
    }

    @Override
    public void deleteTask(String id) {
        try {
            taskMapper.deleteById(id);
        } catch (Exception e) {
            throw new ResourceOperateFailedException("delete task failed by id: " + id, e);
        }
    }
}
