package cc.iteck.rm.model.project;

import cc.iteck.rm.model.AbstractEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("rm_project")
public class ProjectEntity extends AbstractEntity {
    private String code;
    private String name;
    private String description;
    private String ownerUserId;
}
