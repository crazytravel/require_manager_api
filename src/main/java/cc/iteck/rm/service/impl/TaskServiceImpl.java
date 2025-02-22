package cc.iteck.rm.service.impl;

import cc.iteck.rm.exception.ResourceNotFoundException;
import cc.iteck.rm.exception.ResourceOperateFailedException;
import cc.iteck.rm.mapper.TaskMapper;
import cc.iteck.rm.model.project.ProjectDto;
import cc.iteck.rm.model.task.OrderTaskForm;
import cc.iteck.rm.model.task.TaskDto;
import cc.iteck.rm.model.task.TaskEntity;
import cc.iteck.rm.service.ProjectService;
import cc.iteck.rm.service.TaskService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final static String TAIL_TASK_ID = "-1";

    private final ProjectService projectService;
    private final TaskMapper taskMapper;

    public TaskServiceImpl(ProjectService projectService, TaskMapper taskMapper) {
        this.projectService = projectService;
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
    public List<TaskDto> filterTasksWithUser(String userId) {
        List<TaskEntity> tasks = taskMapper.selectList(Wrappers.<TaskEntity>lambdaQuery().eq(TaskEntity::getUserId, userId));
        return tasks.stream().map(taskEntity -> {
            TaskDto task = TaskDto.builder().build();
            BeanUtils.copyProperties(taskEntity, task);
            return task;
        }).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public TaskDto createNewTask(TaskDto taskDto) {
        ProjectDto project = projectService.findProject(taskDto.getProjectId());
        int count = taskMapper.selectCount(Wrappers.<TaskEntity>lambdaQuery()
                .eq(TaskEntity::getProjectId, taskDto.getProjectId())) + 1;
        taskDto.setCode(project.getCode() + '-' + count);
        TaskEntity taskEntity = TaskEntity.builder().build();
        BeanUtils.copyProperties(taskDto, taskEntity);
        TaskEntity tailTask = taskMapper.selectOne(Wrappers.<TaskEntity>lambdaQuery()
                .eq(TaskEntity::getNextId, TAIL_TASK_ID)
                .eq(TaskEntity::getStageId, taskDto.getStageId())
                .eq(TaskEntity::getProjectId, taskDto.getProjectId()));
        taskEntity.setNextId(TAIL_TASK_ID);
        taskMapper.insert(taskEntity);
        if (tailTask != null) {
            tailTask.setNextId(taskEntity.getId());
            taskMapper.updateById(tailTask);
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

    @Transactional
    @Override
    public void deleteTask(String id) {
        TaskEntity currentTask = taskMapper.selectById(id);
        TaskEntity preTask = taskMapper.selectOne(Wrappers.<TaskEntity>lambdaQuery().eq(TaskEntity::getNextId, id));
        if (preTask != null) {
            preTask.setNextId(currentTask.getNextId());
            taskMapper.updateById(preTask);
        }
        taskMapper.deleteById(id);
    }

    @Override
    public List<TaskDto> findSortedTasksByStageId(String stageId) {
        List<TaskEntity> taskEntities = taskMapper.selectList(Wrappers.lambdaQuery(TaskEntity.class)
                .eq(TaskEntity::getStageId, stageId));
        if (taskEntities == null || taskEntities.isEmpty()) {
            return new ArrayList<>();
        }
        TaskEntity tailNode = taskEntities.stream().filter(taskEntity ->
                TAIL_TASK_ID.equals(taskEntity.getNextId())).findFirst().orElseThrow();
        TaskDto dto = TaskDto.builder().build();
        BeanUtils.copyProperties(tailNode, dto);
        List<TaskDto> sortedTasks = new ArrayList<>();
        sortedTasks.add(dto);
        sort(taskEntities, dto.getId(), sortedTasks);
        Collections.reverse(sortedTasks);
        return sortedTasks;
    }

    @Transactional
    @Override
    public TaskDto reorderTaskList(String taskId, OrderTaskForm orderTaskForm) {

        List<TaskDto> destDtoList = this.findSortedTasksByStageId(orderTaskForm.getDestinationStageId());
        TaskEntity srcTask = taskMapper.selectById(taskId);
        if (orderTaskForm.getSourceStageId().equals(orderTaskForm.getDestinationStageId())) {
            return sameStageReorder(taskId, orderTaskForm, destDtoList, srcTask);
        }
        return changeTaskStageAndReorder(taskId, orderTaskForm, destDtoList, srcTask);

    }

    private TaskDto changeTaskStageAndReorder(String taskId, OrderTaskForm orderTaskForm, List<TaskDto> destDtoList, TaskEntity srcTask) {
        // remove old list
        //  get previous node by current task id
        TaskEntity preTask = taskMapper.selectOne(Wrappers.<TaskEntity>lambdaQuery().eq(TaskEntity::getNextId, taskId));
        String srcTaskNextId = srcTask.getNextId();
        if (preTask != null) {
            // remove old list
            preTask.setNextId(srcTaskNextId);
            taskMapper.updateById(preTask);
        }
        // add new list
        if (destDtoList == null || destDtoList.size() == 0) {
            srcTask.setNextId(TAIL_TASK_ID);
            srcTask.setStageId(orderTaskForm.getDestinationStageId());
            taskMapper.updateById(srcTask);
            return this.findTask(srcTask.getId());
        }
        int destIndex = orderTaskForm.getDestinationIndex();
        if (destIndex == destDtoList.size()) {
            TaskDto destTaskDto = destDtoList.get(destIndex - 1);
            TaskEntity destTask = TaskEntity.builder().build();
            BeanUtils.copyProperties(destTaskDto, destTask);
            srcTask.setNextId(TAIL_TASK_ID);
            srcTask.setStageId(destTaskDto.getStageId());
            destTask.setNextId(taskId);
            taskMapper.updateById(srcTask);
            taskMapper.updateById(destTask);
            return this.findTask(taskId);
        }
        TaskDto destTaskDto = destDtoList.get(destIndex);
        TaskEntity newPreTask = taskMapper.selectOne(Wrappers.<TaskEntity>lambdaQuery()
                .eq(TaskEntity::getNextId, destTaskDto.getId())
                .eq(TaskEntity::getStageId, destTaskDto.getStageId()));
        if (newPreTask != null) {
            newPreTask.setNextId(taskId);
            taskMapper.updateById(newPreTask);
        }
        srcTask.setNextId(destTaskDto.getId());
        srcTask.setStageId(destTaskDto.getStageId());
        taskMapper.updateById(srcTask);
        return this.findTask(taskId);
    }

    private TaskDto sameStageReorder(String taskId, OrderTaskForm orderTaskForm, List<TaskDto> destDtoList, TaskEntity srcTask) {
        if (orderTaskForm.getSourceIndex() > orderTaskForm.getDestinationIndex()) {
            // same stage and change sequence
            int destIndex = orderTaskForm.getDestinationIndex();
            TaskDto destTaskDto = destDtoList.get(destIndex);
            TaskEntity destTask = TaskEntity.builder().build();
            BeanUtils.copyProperties(destTaskDto, destTask);

            TaskEntity preTask = taskMapper.selectOne(Wrappers.<TaskEntity>lambdaQuery().eq(TaskEntity::getNextId, destTask.getId()));
            if (preTask != null) {
                preTask.setNextId(taskId);
                taskMapper.updateById(preTask);
                destTask.setNextId(srcTask.getNextId());
                srcTask.setNextId(destTask.getId());
                taskMapper.updateById(srcTask);
                taskMapper.updateById(destTask);
            } else {
                String originalSrcTaskNextId = srcTask.getNextId();
                srcTask.setNextId(destTask.getId());
                TaskEntity srcPreTask = taskMapper.selectOne(Wrappers.<TaskEntity>lambdaQuery().eq(TaskEntity::getNextId, taskId));
                srcPreTask.setNextId(originalSrcTaskNextId);
                taskMapper.updateById(srcTask);
                taskMapper.updateById(srcPreTask);
            }
        } else {
            // same stage and change sequence
            TaskEntity preTask = taskMapper.selectOne(Wrappers.<TaskEntity>lambdaQuery().eq(TaskEntity::getNextId, taskId));
            if (preTask != null) {
                preTask.setNextId(srcTask.getNextId());
                taskMapper.updateById(preTask);
            }
            int destIndex = orderTaskForm.getDestinationIndex();
            TaskDto destTaskDto = destDtoList.get(destIndex);
            TaskEntity destTask = TaskEntity.builder().build();
            BeanUtils.copyProperties(destTaskDto, destTask);
            String destTaskNextId = destTask.getNextId();
            destTask.setNextId(taskId);
            srcTask.setNextId(destTaskNextId);
            taskMapper.updateById(srcTask);
            taskMapper.updateById(destTask);
        }
        return this.findTask(taskId);
    }

    private void sort(List<TaskEntity> tasks, String currentId, List<TaskDto> sortedTasks) {
        if (sortedTasks.size() == tasks.size()) {
            return;
        }
        tasks.forEach(entity -> {
            String nextId = entity.getNextId();
            if (currentId.equals(nextId)) {
                TaskDto dto = TaskDto.builder().build();
                BeanUtils.copyProperties(entity, dto);
                sortedTasks.add(dto);
                nextId = entity.getId();
                sort(tasks, nextId, sortedTasks);
            }
        });
    }
}
