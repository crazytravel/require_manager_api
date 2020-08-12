package cc.iteck.rm.mapper;

import cc.iteck.rm.model.task.TaskDto;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cc.iteck.rm.model.task.TaskEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskMapper extends BaseMapper<TaskEntity> {

}
