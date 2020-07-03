package cc.iteck.rm.model.task;

import com.baomidou.mybatisplus.annotation.TableName;
import cc.iteck.rm.model.AbstractEntity;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("rm_task")
public class TaskEntity extends AbstractEntity {
    private String content;
    private String projectId;
    private String stageId;
    private String status;
    private String previousId;
    private String nextId;
}
