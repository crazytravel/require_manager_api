package cc.iteck.rm.model.task;

import com.baomidou.mybatisplus.annotation.TableName;
import cc.iteck.rm.model.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("rm_task")
public class TaskEntity extends AbstractEntity {
}
