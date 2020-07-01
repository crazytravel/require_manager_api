package cc.iteck.rm.model.stage;

import cc.iteck.rm.model.AbstractEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("rm_stage")
public class StageEntity extends AbstractEntity {
    private String name;
    private String projectId;
}
